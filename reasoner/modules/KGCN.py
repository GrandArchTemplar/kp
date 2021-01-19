import numpy as np
import torch
import math
import sklearn
np.random.seed(0)
from modules.NeighborAggregator import NeighborAggregator
class KGCN(torch.nn.Module):
    def __init__(self, n_user, n_entity, n_relation, adj_entity, adj_relation, batch_size):
        super(KGCN, self).__init__()
        self.adj_entity = adj_entity
        self.adj_relation = adj_relation
        self.n_iter = 2
        self.n_neighbor = 4
        self.dim = 32
        self.n_user = n_user
        self.l2_weight = 1e-7
        self.aggregator_class = NeighborAggregator
        self.batch_size = batch_size
        self.entity_emb_matrix = torch.nn.init.uniform_(torch.empty(n_entity, self.dim))
        # self.relation_emb_matrix =  torch.nn.init.uniform_(torch.empty(n_relation, self.dim))
        self.aggregators = []
        self.relation_emb_matrix = torch.nn.Embedding(n_relation, self.dim,
                                             sparse=True)  # torch.nn.init.xavier_uniform_(torch.empty(n_relation, self.dim))
        self.user_emb_matrix = torch.nn.Embedding(n_user, self.dim, sparse=True)
        # self.user_emb_matrix=torch.nn.init.uniform_(torch.empty(n_user, self.dim))
        self.conv1 = self.aggregator_class(self.dim, act='tanh')
        self.conv2 = self.aggregator_class(self.dim, act='relu')
        self.reset_parameters()

    def reset_parameters(self):
        self.relation_emb_matrix.reset_parameters()
        self.user_emb_matrix.reset_parameters()

    def get_neighbors(self, seeds):
        entities = [seeds]
        relations = []
        for i in range(self.n_iter):
            neighbor_entities = (torch.tensor(self.adj_entity[entities[i]])).view(
                [self.batch_size, -1])  # self.n_neighbor**(i+1)])
            neighbor_relations = (torch.tensor(self.adj_relation[entities[i]])).view(
                [self.batch_size, -1])  # self.n_neighbor**(i+1)])
            entities.append(neighbor_entities)
            relations.append(neighbor_relations)
        return entities, relations

    def forward(self, user_indices, item_indices, device):
        user_embeddings = self.user_emb_matrix.weight[user_indices].to(device)
        entities, relations = self.get_neighbors(item_indices)
        # store all aggregators
        entity_vectors = [self.entity_emb_matrix[torch.LongTensor(i)] for i in entities]
        relation_vectors = [self.relation_emb_matrix.weight[torch.LongTensor(i)] for i in relations]
        for i in range(self.n_iter):
            if i == self.n_iter - 1:
                aggregator = self.conv1
            else:
                aggregator = self.conv2
            self.aggregators.append(aggregator)

            entity_vectors_next_iter = []
            for hop in range(self.n_iter - i):
                shape2 = [self.batch_size, -1, self.n_neighbor, self.dim]
                vector = aggregator(self_vectors=entity_vectors[hop],
                                    neighbor_vectors=(entity_vectors[hop + 1]).view(shape2),
                                    neighbor_relations=(relation_vectors[hop]).view(shape2),
                                    user_embeddings=user_embeddings, batch_size=self.batch_size, device=device)
                entity_vectors_next_iter.append(vector)
            entity_vectors = entity_vectors_next_iter

        res = (entity_vectors[0]).view([self.batch_size, self.dim])
        scores = (user_embeddings * res).sum(axis=1)

        scores_normalized = (scores.sigmoid())
        return scores_normalized

    def inference(self, user_indices, item_indices, device):
        user_embeddings = self.user_emb_matrix.weight[user_indices]  # .to(device)
        entities = [item_indices]
        relations = []
        for i in range(self.n_iter):
            neighbor_entities = (torch.tensor(self.adj_entity[entities[i]])).view(
                [len(item_indices), -1])  # self.n_neighbor**(i+1)])
            neighbor_relations = (torch.tensor(self.adj_relation[entities[i]])).view(
                [len(item_indices), -1])  # self.n_neighbor**(i+1)])
            entities.append(neighbor_entities)
            relations.append(neighbor_relations)
        entity_vectors = [self.entity_emb_matrix[torch.LongTensor(i)] for i in entities]
        relation_vectors = [self.relation_emb_matrix.weight[torch.LongTensor(i)] for i in relations]
        for i in range(self.n_iter):
            entity_vectors_next_iter = []
            for hop in range(self.n_iter - i):
                shape2 = [len(item_indices), -1, self.n_neighbor, self.dim]
                vector = self.aggregators[i](self_vectors=entity_vectors[hop],
                                             neighbor_vectors=(entity_vectors[hop + 1]).view(shape2),
                                             neighbor_relations=(relation_vectors[hop]).view(shape2),
                                             user_embeddings=user_embeddings, batch_size=len(item_indices),
                                             device='cpu')
                entity_vectors_next_iter.append(vector)
            entity_vectors = entity_vectors_next_iter

        res = (entity_vectors[0]).view([len(item_indices), self.dim])
        scores = (user_embeddings * res).sum(axis=1)

        scores_normalized = (scores.sigmoid())
        return scores_normalized

    def loss(self, out, labels, device):
        loss = 0
        labels = torch.tensor(labels).to(device)
        base_loss = (-labels * ((out).log()) - (1 - labels) * ((1 - out).log())).mean()
        l2_loss = 0
        for p in self.parameters():
            l2_loss = l2_loss + ((p * p).sum()) / 2
        loss = self.l2_weight * l2_loss + base_loss
        return loss

    def ctr(self, scores, labels):  # auc, f-1
        scores = scores.cpu().detach().numpy()
        auc = sklearn.metrics.roc_auc_score(y_true=labels, y_score=scores)
        scores[scores >= 0.5] = 1
        scores[scores < 0.5] = 0
        f1 = sklearn.metrics.f1_score(y_true=labels, y_pred=scores)
        return auc, f1

    def train_acc(self, scores):  # auc, f-1
        scores = scores.cpu().detach().numpy()
        scores[scores >= 0.5] = 1
        scores[scores < 0.5] = 0
        return sum(scores) / len(scores)
