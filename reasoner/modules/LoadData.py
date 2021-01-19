#загружаем данные для тренировки, граф знаний и конструируем матрицы смежности 
import os
import numpy as np

def load_data():
    n_user, n_item, train_data, eval_data, test_data = load_rating()
    n_entity, n_relation, adj_entity, adj_relation,kg = load_kg()
    print('data loaded.')
    return n_user, n_item, n_entity, n_relation, train_data, eval_data, test_data, adj_entity, adj_relation,kg

def load_rating():
    print('reading rating file ...')
    # reading rating file
    rating_file =  './data_for_training/ratings_final'
    if os.path.exists(rating_file + '.npy'):
        rating_np = np.load(rating_file + '.npy')
    else:
        rating_np = np.loadtxt(rating_file + '.txt', dtype=np.int64)
        np.save(rating_file + '.npy', rating_np)
  
    rating_np_new=rating_np[:int(rating_np.shape[0]/3)]

    n_user = len(set(rating_np_new[:, 0]))
    n_item = len(set(rating_np_new[:, 1]))
    train_data, eval_data, test_data = dataset_split(rating_np_new)

    return n_user, n_item, train_data, eval_data, test_data

def dataset_split(rating_np):
    print('splitting dataset ...')
    # train:eval:test = 6:2:2
    eval_ratio = 0.2
    test_ratio = 0.2
    n_ratings = rating_np.shape[0]

    eval_indices = np.random.choice(list(range(n_ratings)), size=int(n_ratings * eval_ratio), replace=False)
    left = set(range(n_ratings)) - set(eval_indices)
    test_indices = np.random.choice(list(left), size=int(n_ratings * test_ratio), replace=False)
    train_indices = list(left - set(test_indices))

    train_data=rating_np[train_indices]
    test_data=rating_np[test_indices]
    eval_data=rating_np[eval_indices]
    return train_data, eval_data, test_data


def load_kg():
    print('reading KG file ...')

    # reading kg file
    kg_file = './data_for_training/kg_final_2'
    if os.path.exists(kg_file + '.npy'):
        kg_np = np.load(kg_file + '.npy')
    else:
        #input file - csv, я должна зменить запятые, чтоб np.load смог прочитать 
        fin = open(kg_file+'.txt', "rt")
        fout = open(kg_file+"out.txt", "wt")
        for line in fin:
            fout.write(line.replace(',', '  '))
        fin.close()
        fout.close()
        kg_np = np.loadtxt(kg_file+"out.txt", dtype=np.int64)
        np.save(kg_file + '.npy', kg_np)
    n_entity = max(set(kg_np[:, 2])) + 1#len(set(kg_np[:, 0]) | set(kg_np[:, 2]))
    n_relation = max(set(kg_np[:, 1]))+1
    kg = construct_kg(kg_np)
    adj_entity, adj_relation = construct_adj(kg, n_entity)

    return n_entity, n_relation, adj_entity, adj_relation, kg

def construct_kg(kg_np):
    print('constructing knowledge graph ...')
    kg = dict()
    for triple in kg_np:
        head = triple[0]
        relation = triple[1]
        tail = triple[2]
        # treat the KG as an undirected graph
        if head not in kg:
            kg[head] = []
        kg[head].append((tail, relation))
        if tail not in kg:
            kg[tail] = []
        kg[tail].append((head, relation))
    return kg
  
def construct_adj(kg, entity_num):
    print('constructing adjacency matrix ...')
    # each line of adj_entity stores the sampled neighbor entities for a given entity
    # each line of adj_relation stores the corresponding sampled neighbor relations

    adj_entity = np.zeros([entity_num, 4], dtype=np.int64)
    adj_relation = np.zeros([entity_num, 4], dtype=np.int64)
    for entity in range(entity_num):
        if entity in kg: 
            neighbors = kg[entity]
            n_neighbors = len(neighbors)
            if n_neighbors >= 4:
                sampled_indices = np.random.choice(list(range(n_neighbors)), size=4, replace=False)
            else:
                sampled_indices = np.random.choice(list(range(n_neighbors)), size=4, replace=True)
            adj_entity[entity] = np.array([neighbors[i][0] for i in sampled_indices])
            adj_relation[entity] = np.array([neighbors[i][1] for i in sampled_indices])

    return adj_entity, adj_relation