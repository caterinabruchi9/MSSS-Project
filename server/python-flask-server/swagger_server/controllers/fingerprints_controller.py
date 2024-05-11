import connexion
import six

from swagger_server.models.fingerprint import Fingerprint
from swagger_server.models.db_manager import DBManager
from swagger_server import util


def add_fingerprint(body):
    """Add a new Wi-Fi fingerprint

    :param body: Wi-Fi fingerprint data
    :type body: dict | bytes

    :rtype: json
    """
    if connexion.request.is_json:
        body = Fingerprint.from_dict(connexion.request.get_json())

        map_id = body.map_id
        ssid = body.ssid
        bssid = body.bssid
        rss_index = body.rss
        frequency = body.frequency
        zone = body.zone
        sample = body.sample

        db = DBManager()
        response = db.add_fingerprint(map_id, ssid, bssid, rss_index, frequency, zone, sample)
        db.close_connection()

        return response

    return {"status": 400, "error": "Invalid request (bad JSON object)"}
