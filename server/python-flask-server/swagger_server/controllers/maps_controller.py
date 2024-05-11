import connexion
import six

from swagger_server.models.fingerprint import Fingerprint
from swagger_server.models.map import Map
from swagger_server.models.db_manager import DBManager
from swagger_server import util


def create_map(body):
    """Create a new Map

    :param body: Map data
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = Map.from_dict(connexion.request.get_json())

        map_id = body.map_id
        building_name = body.building_name
        rooms = body.rooms

        db = DBManager()
        response = db.insert_map(map_id, building_name, rooms)
        db.close_connection()

        return response

    return {"status": 400, "error": "Invalid request (bad JSON object)"}


def get_map_by_id(map_id):
    """Find Map by ID

    Returns a single map

    :param mapId: ID of Map to return
    :type mapId: int

    :rtype: Map
    """

    db = DBManager()
    response = db.get_map(map_id)
    db.close_connection()

    return response


def get_maps():
    """Find all the available maps

    :rtype: Map
    """
    
    db = DBManager()
    response = db.get_all_maps()
    db.close_connection()

    return response
