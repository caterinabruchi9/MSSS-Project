import connexion
import six

from swagger_server.models.direction import Direction
from swagger_server.models.db_manager import DBManager
from swagger_server import util


def add_direction(body):
    if connexion.request.is_json:
        body = Direction.from_dict(connexion.request.get_json())

        map_id = body.map_id
        zone = body.zone
        sample = body.sample
        azimut = body.azimut
        info = body.info

        db = DBManager()
        response = db.add_direction(map_id, zone, sample, azimut, info)
        db.close_connection()

        return response

    return {"status": 400, "error": "Invalid request (bad JSON object)"}


def get_directions_by_id(map_id):

    db = DBManager()
    response = db.get_directions(map_id)
    db.close_connection()

    return response
