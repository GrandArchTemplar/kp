# pip install torch==1.7.1+cpu torchvision==0.8.2+cpu torchaudio===0.7.2 -f https://download.pytorch.org/whl/torch_stable.html
from modules.KGCN import KGCN
from modules.NeighborAggregator import NeighborAggregator
import numpy as np
import torch
import math
import sklearn

np.random.seed(0)

### здесь начинается все главное ###

# Получаем от пользователя список из индексов фильмов new_user = [id_1,id_2,id_3]
new_user = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

path = './'
device = 'cpu'

# загружаем уже натренированную модель
model = torch.load(path + 'model.pt', map_location=torch.device('cpu'))
model.eval()

# загрузим юзеров, с которыми будем сравнивать
train_data_to_compare_np = np.load(path + 'train_data_to_compare.npy', allow_pickle=True)
train_data_to_compare = train_data_to_compare_np.item()


# основная функция
def find_recommendation_for_user(_new_user):
    ind = most_common_with(_new_user)  # найдем того юзера из БД с кем вкусы наиболее похожи
    item_indices = list(range(16954))  # индексы фильмов для которых будем искать вероятность
    user_indices = ind.tolist() * len(item_indices)
    return find_recommendation(model, user_indices, item_indices)


def most_common_with(new_u):  # Ищем максимально похожего юзера + если будет несколько. то выбираем рандомного
    list_of_all_users = []
    for u in train_data_to_compare:
        equlity = len((set(new_u) & set(train_data_to_compare[u])))
        list_of_all_users.append(equlity)
    return np.random.choice(np.argwhere(list_of_all_users == np.amax(list_of_all_users)).flatten(), 1)


# функция, возвращающая ОТСОРТИРОВАННЫЙ список из 50 наиболее подходящих фильмов
def find_recommendation(model, user_indices, item_indices):
    out = model.inference(user_indices, item_indices, device)
    out = np.argsort(out.tolist())
    return out[len(out) - 50:]


print(find_recommendation_for_user(new_user))
