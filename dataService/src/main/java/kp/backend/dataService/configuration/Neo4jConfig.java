package kp.backend.dataService.configuration;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kp.neo4j")
public class Neo4jConfig {

    @Getter
    @Setter
    private String url;

    @Bean(name = "neo4jDriver")
    public Driver driver(){
        return GraphDatabase.driver(url);
    }

}
