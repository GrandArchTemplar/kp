
import torch
LAYER_IDS = {}

def get_layer_id(layer_name=''):
    if layer_name not in LAYER_IDS:
        LAYER_IDS[layer_name] = 0
        return 0
    else:
        LAYER_IDS[layer_name] += 1
        return LAYER_IDS[layer_name]


class NeighborAggregator(torch.nn.Module):
    def __init__(self, dim, act, name=None):
        super(NeighborAggregator, self).__init__()
        if not name:
            layer = self.__class__.__name__.lower()
            name = layer + '_' + str(get_layer_id(layer))
        self.name = name
        self.act = act
        self.dim = 32
        self.bias = torch.nn.parameter.Parameter(torch.FloatTensor(32))
        self.weights = torch.nn.parameter.Parameter(torch.FloatTensor(32, 32))
        self.reset_parameters()

    def reset_parameters(self):
        torch.nn.init.uniform_(self.bias, -1, 1)
        torch.nn.init.uniform_(self.weights, -1, 1)

    def __call__(self, self_vectors, neighbor_vectors, neighbor_relations, user_embeddings, batch_size, device):
        outputs = self._call(self_vectors, neighbor_vectors, neighbor_relations, user_embeddings, batch_size, device)
        return outputs

    def _mix_neighbor_vectors(self, neighbor_vectors, neighbor_relations, user_embeddings, batch_size, device):
        avg = False
        if not avg:
            user_embeddings = user_embeddings.view([batch_size, 1, 1, self.dim])
            neighbor_relations = neighbor_relations.to(device)
            user_relation_scores = (user_embeddings * neighbor_relations).mean(axis=-1)
            user_relation_scores_normalized = user_relation_scores.softmax(dim=-1)
            user_relation_scores_normalized = torch.unsqueeze(user_relation_scores_normalized, axis=-1)
            neighbor_vectors = neighbor_vectors.to(device)
            neighbors_aggregated = (user_relation_scores_normalized * neighbor_vectors).mean(axis=2)
        else:
            neighbors_aggregated = (neighbor_vectors).mean(axis=2)

        return neighbors_aggregated

    def _call(self, self_vectors, neighbor_vectors, neighbor_relations, user_embeddings, batch_size, device):
        neighbors_agg = self._mix_neighbor_vectors(neighbor_vectors, neighbor_relations, user_embeddings, batch_size,
                                                   device)
        output = (neighbors_agg).view([-1, self.dim]).to(device)
        output = torch.matmul(output, self.weights) + self.bias
        output = output.view([batch_size, -1, self.dim])
        if self.act == 'tanh':
            return (output.tanh())
        else:
            return (output.relu())
