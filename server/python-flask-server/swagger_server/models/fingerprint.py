# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server import util


class Fingerprint(Model):
    def __init__(self, map_id: int=None, ssid: str=None, bssid: str=None, rss: int=None, frequency: int=None, zone: int=None, sample: int=None):
        self.swagger_types = {
            'map_id': int,
            'ssid': str,
            'bssid': str,
            'rss': int,
            'frequency': int,
            'zone': int,
            'sample': int
        }

        self.attribute_map = {
            'map_id': 'map-id',
            'ssid': 'ssid',
            'bssid': 'bssid',
            'rss': 'RSS',
            'frequency': 'frequency',
            'zone': 'zone',
            'sample': 'sample'
        }

        self._map_id = map_id
        self._ssid = ssid
        self._bssid = bssid
        self._rss = rss
        self._frequency = frequency
        self._zone = zone
        self._sample = sample

    @classmethod
    def from_dict(cls, dikt) -> 'Fingerprint':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The Fingerprint of this Fingerprint.  # noqa: E501
        :rtype: Fingerprint
        """
        return util.deserialize_model(dikt, cls)

    @property
    def map_id(self) -> int:
        """Gets the map_id of this Fingerprint.


        :return: The map_id of this Fingerprint.
        :rtype: int
        """
        return self._map_id

    @map_id.setter
    def map_id(self, map_id: int):
        """Sets the map_id of this Fingerprint.


        :param map_id: The map_id of this Fingerprint.
        :type map_id: int
        """
        if map_id is None:
            raise ValueError("Invalid value for `map_id`, must not be `None`")  # noqa: E501

        self._map_id = map_id

    @property
    def ssid(self) -> str:
        """Gets the ssid of this Fingerprint.


        :return: The ssid of this Fingerprint.
        :rtype: str
        """
        return self._ssid

    @ssid.setter
    def ssid(self, ssid: str):
        """Sets the ssid of this Fingerprint.


        :param ssid: The ssid of this Fingerprint.
        :type ssid: str
        """
        if ssid is None:
            raise ValueError("Invalid value for `ssid`, must not be `None`")  # noqa: E501

        self._ssid = ssid

    @property
    def bssid(self) -> str:
        """Gets the bssid of this Fingerprint.


        :return: The bssid of this Fingerprint.
        :rtype: str
        """
        return self._bssid

    @bssid.setter
    def bssid(self, bssid: str):
        """Sets the bssid of this Fingerprint.


        :param bssid: The bssid of this Fingerprint.
        :type bssid: str
        """
        if bssid is None:
            raise ValueError("Invalid value for `bssid`, must not be `None`")  # noqa: E501

        self._bssid = bssid

    @property
    def rss(self) -> int:
        """Gets the rss of this Fingerprint.


        :return: The rss of this Fingerprint.
        :rtype: int
        """
        return self._rss

    @rss.setter
    def rss(self, rss: int):
        """Sets the rss of this Fingerprint.


        :param rss: The rss of this Fingerprint.
        :type rss: int
        """
        if rss is None:
            raise ValueError("Invalid value for `rss`, must not be `None`")  # noqa: E501

        self._rss = rss

    @property
    def frequency(self) -> int:
        """Gets the frequency of this Fingerprint.


        :return: The frequency of this Fingerprint.
        :rtype: int
        """
        return self._frequency

    @frequency.setter
    def frequency(self, frequency: int):
        """Sets the frequency of this Fingerprint.


        :param frequency: The frequency of this Fingerprint.
        :type frequency: int
        """
        if frequency is None:
            raise ValueError("Invalid value for `frequency`, must not be `None`")  # noqa: E501

        self._frequency = frequency

    @property
    def zone(self) -> int:
        """Gets the zone of this Fingerprint.


        :return: The zone of this Fingerprint.
        :rtype: int
        """
        return self._zone

    @zone.setter
    def zone(self, zone: int):
        """Sets the zone of this Fingerprint.


        :param zone: The zone of this Fingerprint.
        :type zone: int
        """
        if zone is None:
            raise ValueError("Invalid value for `zone`, must not be `None`")  # noqa: E501

        self._zone = zone

    @property
    def sample(self) -> int:
        """Gets the sample of this Fingerprint.


        :return: The sample of this Fingerprint.
        :rtype: int
        """
        return self._sample

    @sample.setter
    def sample(self, sample: int):
        """Sets the sample of this Fingerprint.


        :param sample: The sample of this Fingerprint.
        :type sample: int
        """
        if sample is None:
            raise ValueError("Invalid value for `sample`, must not be `None`")  # noqa: E501

        self._sample = sample