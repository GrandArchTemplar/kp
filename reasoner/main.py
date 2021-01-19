import json
from modules.KGCN import KGCN
from modules.NeighborAggregator import NeighborAggregator
import flask
from flask import Response, request

import MainScript as ms

app = flask.Flask(__name__)


@app.route('/reasoner/api/v1/recommend', methods=['POST'])
def recommend():

    new_user = request.get_json()
    recommendations = ms.find_recommendation_for_user(new_user)
    return Response(json.dumps(recommendations.tolist()),  mimetype='application/json')


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8955)
