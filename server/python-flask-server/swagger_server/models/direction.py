# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime

from typing import List, Dict

from swagger_server.models.base_model_ import Model
from swagger_server import util


class Direction(Model):

    def __init__(self, map_id: int=None, zone: int=None, sample: int=None, azimut: float=None, info: str=None):
        self.swagger_types = {
            'map_id': int,
            'zone': int,
            'sample': int,
            'azimut': float,
            'info': str
        }

        self.attribute_map = {
            'map_id': 'map-id',
            'zone': 'zone',
            'sample': 'sample',
            'azimut': 'azimut',
            'info': 'info'
        }

        self._map_id = map_id
        self._zone = zone
        self._sample = sample
        self._azimut = azimut
        self._info = info

    @classmethod
    def from_dict(cls, dikt) -> 'Direction':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The Direction of this Direction.
        :rtype: Direction
        """
        return util.deserialize_model(dikt, cls)

    @property
    def map_id(self) -> int:
        """Gets the map_id of this Direction.


        :return: The map_id of this Direction.
        :rtype: int
        """
        return self._map_id

    @map_id.setter
    def map_id(self, map_id: int):
        """Sets the map_id of this Direction.


        :param map_id: The map_id of this Direction.
        :type map_id: int
        """

        self._map_id = map_id

    @property
    def zone(self) -> int:
        """Gets the zone of this Direction.


        :return: The zone of this Direction.
        :rtype: int
        """
        return self._zone

    @zone.setter
    def zone(self, zone: int):
        """Sets the zone of this Direction.


        :param zone: The zone of this Direction.
        :type zone: int
        """
        if zone is None:
            raise ValueError("Invalid value for `zone`, must not be `None`")  # noqa: E501

        self._zone = zone

    @property
    def sample(self) -> int:
        """Gets the sample of this Direction.


        :return: The sample of this Direction.
        :rtype: int
        """
        return self._sample

    @sample.setter
    def sample(self, sample: int):
        """Sets the sample of this Direction.


        :param sample: The sample of this Direction.
        :type sample: int
        """
        if sample is None:
            raise ValueError("Invalid value for `sample`, must not be `None`")  # noqa: E501

        self._sample = sample

    @property
    def azimut(self) -> float:
        """Gets the azimut of this Direction.


        :return: The azimut of this Direction.
        :rtype: float
        """
        return self._azimut

    @azimut.setter
    def azimut(self, azimut: float):
        """Sets the azimut of this Direction.


        :param azimut: The azimut of this Direction.
        :type azimut: float
        """
        if azimut is None:
            raise ValueError("Invalid value for `azimut`, must not be `None`")  # noqa: E501

        self._azimut = azimut