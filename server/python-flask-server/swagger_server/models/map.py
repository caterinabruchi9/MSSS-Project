# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime

from typing import List, Dict

from swagger_server.models.base_model_ import Model
from swagger_server import util


class Map(Model):

    def __init__(self, map_id: int=None, building_name: str=None, rooms: int=None):
        self.swagger_types = {
            'map_id': int,
            'building_name': str,
            'rooms': int
        }

        self.attribute_map = {
            'map_id': 'map-id',
            'building_name': 'building-name',
            'rooms': 'rooms'
        }

        self._map_id = map_id
        self._building_name = building_name
        self._rooms = rooms

    @classmethod
    def from_dict(cls, dikt) -> 'Map':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The Map of this Map.
        :rtype: Map
        """
        return util.deserialize_model(dikt, cls)

    @property
    def map_id(self) -> int:
        """Gets the map_id of this Map.


        :return: The map_id of this Map.
        :rtype: int
        """
        return self._map_id

    @map_id.setter
    def map_id(self, map_id: int):
        """Sets the map_id of this Map.


        :param map_id: The map_id of this Map.
        :type map_id: int
        """

        self._map_id = map_id

    @property
    def building_name(self) -> str:
        """Gets the building_name of this Map.


        :return: The building_name of this Map.
        :rtype: str
        """
        return self._building_name

    @building_name.setter
    def building_name(self, building_name: str):
        """Sets the building_name of this Map.


        :param building_name: The building_name of this Map.
        :type building_name: str
        """
        if building_name is None:
            raise ValueError("Invalid value for `building_name`, must not be `None`")

        self._building_name = building_name

    @property
    def rooms(self) -> int:
        """Gets the rooms of this Map.


        :return: The rooms of this Map.
        :rtype: int
        """
        return self._rooms

    @rooms.setter
    def rooms(self, rooms: int):
        """Sets the rooms of this Map.


        :param rooms: The rooms of this Map.
        :type rooms: int
        """
        if rooms is None:
            raise ValueError("Invalid value for `rooms`, must not be `None`")

        self._rooms = rooms