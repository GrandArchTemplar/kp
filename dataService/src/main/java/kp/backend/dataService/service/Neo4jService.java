package kp.backend.dataService.service;

import com.google.common.collect.Lists;
import kp.backend.dataService.configuration.Neo4jConfig;
import kp.backend.dataService.dto.GraphDTO;
import kp.backend.dataService.exception.KPBusinessException;
import kp.backend.dataService.model.KG;
import kp.backend.dataService.model.Node;
import kp.backend.dataService.model.Relation;
import kp.backend.dataService.model.Triplet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;

@Component
public class Neo4jService {
    private static CSVParser superCool;
    private static List<CSVRecord> superWhiteList;
    private static CSVParser superAllFilms;
    static List<CSVRecord> superCoolList;
    private KG kg;

    private <T, U, R> Stream<R> zip(Stream<T> tStream, Stream<U> uStream, BiFunction<T, U, R> zipper) {
        return StreamSupport.stream(spliteratorUnknownSize(
                new Iterator<R>() {
                    final Iterator<T> itT = tStream.iterator();
                    final Iterator<U> itU = uStream.iterator();

                    @Override
                    public boolean hasNext() {
                        return itT.hasNext() && itU.hasNext();
                    }

                    @Override
                    public R next() {
                        return zipper.apply(itT.next(), itU.next());
                    }
                },
                0),
                false
        );
    }

    @Autowired
    public Neo4jService(@Qualifier("neo4jDriver") Driver driver, ExtractorService extractorService) {
        /*Driver driver = GraphDatabase.driver("bolt://localhost:7687");
        String res = driver.session().writeTransaction(transaction -> transaction
                .run("load csv with headers from \"file:///C:/Users/GrandArchTemplar/IdeaProjects/kpkp/IMDb%20movies.csv\" as line" +
                        "\nload csv with headers from \"file:///C:/Users/GrandArchTemplar/IdeaProjects/kpkp/links.csv\" as filtrator" +
                        "\ncreate (res:Film { id: line.imdb_title_id, title: line.title, year: toInteger(line.year) })" +
                        "\ncreate (fil:Filter { id: filtrator.imdbId })" +
                        "\nwith res, fil" +
                        "\nmatch (res:Film) where res.id in fil.id" +
                        "\nreturn res")
                        //"\nreturn [x in res where x.id in filtrator.imdbId]")
                .list()
                .stream().map(x -> x.fields().get(0).value().get("title"))
                .limit(20)
                .collect(Collectors.toList())
                .toString());
        System.out.println(res);*/
        //driver.close();
        //new Main().generateCool();

        this.driver = driver;

        //fetch current snapshot
        //String csvString = extractorService.getSnapshotAsCSVString();
        //if (csvString == null || csvString.equals("")) throw new KPBusinessException("Couldn't fetch");

        try {
            superAllFilms = new CSVParser(
                    //new StringReader(csvString),
                    new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/IMDb movies.csv"))),
                    CSVFormat.DEFAULT.withFirstRecordAsHeader());
            superWhiteList = new CSVParser(
                    new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/links.csv"))),
                    CSVFormat.DEFAULT.withFirstRecordAsHeader()).getRecords();
            try {
                superCool = new CSVParser(
                        new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/coolfilms.csv"))),
                        CSVFormat.DEFAULT.withFirstRecordAsHeader());
            } catch (IOException e) {
                generateCool();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        var x = driver.session();
        kg = generateInternalFullKg(superCool, superWhiteList);
        if (isEmptyDB(x)) {
            CreateDataBaseNodes(x, kg);
            CreateDataBaseRelations(x, kg);
        }
        x.close();
        //System.out.println(getFilmsForGleb(kg));
        //System.out.println(getVerticesForGleb(kg));
        //System.out.println(getEdgesForGleb(kg));
    }

    public GraphDTO getGraph() {
        updateKG();
        return new GraphDTO(getVertices(), getEdges());
    }

    public String getFilms() {
        updateKG();
        return _getFilms();
    }

    public List<Integer> checkFilmsExist(List<Integer> films){
        var x = driver.session();
        List<Integer> result = checkExistence(x, films);
        x.close();
        return result;
    }

    private String _getFilms() {
        return "id,film_name\n" + kg.nodes.stream().filter(x -> x.isFilm).map(x -> x.id + "," + x.representation + "\n").reduce("",(x, y) -> x + y);
    }

    private String getVertices() {
        return "id,name\n" + kg.nodes.stream().map(x -> x.id + "," + x.representation + "\n").reduce("",(x, y) -> x + y);
    }

    private String getEdges() {
        return "from,with,to\n" + kg.relations.stream().limit(20).map(x -> x.from.id + "," + x.with.representation + "," + x.to.id + "\n").reduce("",(x, y) -> x + y);
    }

    private List<Integer> checkExistence(Session x, List<Integer> idList) {
        return idList.stream().filter(id -> !x.writeTransaction(t -> t
                .run("match (a: Node) where a.id = " + id + " return a")
                .hasNext()
        )).collect(Collectors.toList());
    }

    private boolean isEmptyDB(Session x) {
        boolean[] res = {true};
        x.writeTransaction(t -> res[0] = t.run("match (n) return n;").hasNext());
        return !res[0];
    }

    private void updateKG(){
        var x = driver.session();
        //kg = fillKG(x);
        x.close();
    }

    private void CreateDataBaseNodes(Session x, KG kg) {
        Lists.partition(new ArrayList<>(kg.nodes), 5).forEach(batch ->
        x.writeTransaction(transaction -> transaction
                .run("create " + batch
                        .stream()
                        .map(n -> "(:Node { id:" + n.id + "," + "title:\"" + n.representation.replace('\"', '\'') + "isFilm:" + n.isFilm + "\"})")
                        .map(StringBuilder::new)
                        .reduce(new StringBuilder(), (v, s) -> {
                            if (v.length() == 0) return v.append(s);
                            else return v.append(",").append(s);
                        }).toString())));
    }

    private void CreateDataBaseRelations(Session x, KG kg) {
        kg.relations.forEach(r -> x.writeTransaction(transaction -> transaction
                        .run("match (a:Node), (b:Node)\n"
                                + "where a.id = " + r.from.id + " and " + "b.id = " + r.to.id + "\n"
                                + "create (a)-[:" + r.with.representation + "]->(b);\n")
                ));
    }

    private KG fillKG(Session x) {
        Set<Node> nodes = new HashSet<>();
        List<Triplet<Node, Relation, Node>> relations = new ArrayList<>();
        x.writeTransaction(t -> t
                .run("match (n:Node) return n")
                .stream().map(y -> nodes.add(new Node(
                        y.fields().get(0).value().get("id").asInt(),
                        y.fields().get(0).value().get("representation").asString(),
                        y.fields().get(0).value().get("isFilm").asBoolean()))));
        x.writeTransaction(t -> t
                .run("match (n:Node)-[r]-(m:Node) return n,r,m")
                .stream().map(y -> relations.add(new Triplet<>(
                        new Node(
                                y.fields().get(0).value().get("id").asInt(),
                                y.fields().get(0).value().get("representation").asString(),
                                y.fields().get(0).value().get("isFilm").asBoolean()),
                        new Relation(
                                y.fields().get(1).value().get("id").asInt(),
                                y.fields().get(1).value().get("representation").asString()),
                        new Node(
                                y.fields().get(2).value().get("id").asInt(),
                                y.fields().get(2).value().get("representation").asString(),
                                y.fields().get(2).value().get("isFilm").asBoolean())
                ))));
        return new KG(nodes, relations);
    }

    private KG generateInternalFullKg(CSVParser cool, List<CSVRecord> whiteList) {
        try {
            List<CSVRecord> coolList = cool.getRecords();
            Set<Node> nodes = generateAllNodes(coolList, whiteList);
            List<Triplet<String, String, String>> linx = generateAllLinks(coolList);
            return new KG(nodes, patchRelationTriplets(patchNodeTriplets(linx, nodes), Relation.relations));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new KG(new HashSet<>(), new LinkedList<>());
    }

//    public static void generateKg(CSVParser cool, List<CSVRecord> whiteList) {
//        try {
//            new FileWriter("C:\\Users\\GrandArchTemplar\\IdeaProjects\\kpkp\\kg.csv");
//            Files.write(Paths.get("C:\\Users\\GrandArchTemplar\\IdeaProjects\\kpkp\\kg.csv"),
//                    generateInternalFullKg(cool, whiteList).relations
//                            .stream()
//                            .map(x -> x.from.id + "," + x.with.id + "," + (x.to == null ? x : x.to.id))
//                            .collect(Collectors.toList()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private Set<Node> generateFilmNodes(List<CSVRecord> films, List<CSVRecord> linker) {
        List<String> a = new ArrayList<>();
        films.forEach(film -> {
            linker
                    .stream()
                    .parallel()
                    .filter(x -> ("tt" + x.get("imdbId")).equals(film.get("imdb_title_id")))
                    .forEach(x -> a.add(film.get("title")));
        });
        return zip(IntStream.range(0, a.size()).boxed(), a.stream(), (x, y) -> new Node(x, y, true)).collect(Collectors.toSet());
    }

    private Set<Node> generateAllNodes(List<CSVRecord> films, List<CSVRecord> linker) {
        Set<Node> filmNodes = generateFilmNodes(films, linker);
        Set<String> entities = films
                .stream()
                .flatMap(x -> generateLinks(x).stream())
                .map(x -> x.to)
                .map(String::trim)
                .collect(Collectors.toSet());
        List<Node> entitiesNodes = zip(
                IntStream.range(filmNodes.size(),
                        filmNodes.size() + entities.size()).boxed(), entities.stream(),
                (x, y) -> new Node(x, y, false))
                .collect(Collectors.toList());
        Set<Node> res = new HashSet<>();
        res.addAll(filmNodes);
        res.addAll(entitiesNodes);
        return res;
    }

    private <T> List<Triplet<Node, T, Node>> patchNodeTriplets(List<Triplet<String, T, String>> triplets, Set<Node> dict) {
        Map<String, Node> coolDick = dict.stream().collect(Collectors.toMap(x -> x.representation, x -> x));
        return triplets
                .stream()
                .filter(x -> coolDick.containsKey(x.from) && coolDick.containsKey(x.to))
                .map(x -> new Triplet<>(coolDick.get(x.from), x.with, coolDick.get(x.to)))
                .collect(Collectors.toList());
    }

    private <T> List<Triplet<T, Relation, T>> patchRelationTriplets(List<Triplet<T, String, T>> triplets, List<Relation> dict) {
        Map<String, Relation> coolDick = dict.stream().collect(Collectors.toMap(x -> x.representation, x -> x));
        return triplets
                .stream()
                .map(x -> new Triplet<>(x.from, coolDick.get(x.with), x.to))
                .collect(Collectors.toList());
    }

    private List<Triplet<String, String, String>> generateAllLinks(List<CSVRecord> records) {
        return records.stream().flatMap(x -> generateLinks(x).stream()).collect(Collectors.toList());
    }

    private static Boolean notNull(String s) {
        return !s.equals("") && !s.equals("null");
    }

    private List<Triplet<String, String, String>> generateLinks(CSVRecord record) {
        String filmName = record.get("title").trim();

        List<Triplet<String, String, String>> a = new ArrayList<>();
        Arrays.stream(record.get("actors").split(","))
                .filter(Neo4jService::notNull)
                .forEach(actor -> a.add(new Triplet<>(filmName, "contains_as_actor", actor.trim())));
        Arrays.stream(record.get("genre").split(","))
                .filter(Neo4jService::notNull)
                .forEach(genre -> a.add(new Triplet<>(filmName, "has_genre", genre.trim())));
        Arrays.stream(record.get("director").split(","))
                .filter(Neo4jService::notNull)
                .forEach(director -> a.add(new Triplet<>(filmName, "directed_by", director.trim())));
        Arrays.stream(record.get("writer").split(","))
                .filter(Neo4jService::notNull)
                .forEach(writer -> a.add(new Triplet<>(filmName, "written_by", writer.trim())));
        BiConsumer<String, String> f = (fieldName, relationName) -> {
            String tmp = record.get(fieldName).trim();
            if (notNull(tmp)) {
                a.add(new Triplet<>(filmName, relationName, tmp));
            }
        };
        //f.accept("director", "directed_by");
        f.accept("country", "created_in_country");
        f.accept("year", "created_in_year");
        f.accept("duration", "has_duration");
        f.accept("language", "create_with_lang");
        //f.accept("writer", "written_by");
        f.accept("production_company", "producted_by");
        f.accept("avg_vote", "rated_by");
        f.accept("metascore", "stared_by");
        return a;
    }

    private void generateCool() {
        try {

            List<CSVRecord> allFilms = superAllFilms.getRecords();
            CSVPrinter printer =
                    new CSVPrinter(new FileWriter(new ClassPathResource("IMDb movies.csv").getFile()),
                            CSVFormat.DEFAULT.withHeader(superAllFilms.getHeaderMap().keySet().toArray(new String[0])));
            printer.printRecords(allFilms
                    .stream()
                    .parallel()
                    .filter(x -> superWhiteList
                            .stream()
                            .anyMatch(y -> x.get("imdb_title_id")
                                    .equals("tt" + y.get("imdbId"))))
                    .toArray());
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Driver driver;
}
