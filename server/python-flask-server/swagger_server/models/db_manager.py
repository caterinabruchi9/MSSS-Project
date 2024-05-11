import sqlite3

class DBManager:
    def __init__(self, db_file='db/db.sqlite'):
        self.conn = sqlite3.connect(db_file)
        self.cursor = self.conn.cursor()


    def insert_map(self, map_id, building_name, rooms):
        try:
            self.cursor.execute('''
                SELECT * FROM `maps` WHERE `map_id` = ? AND `building_name` = ? AND `rooms` = ?
            ''', (int(map_id), str(building_name), int(rooms), ))
            result = self.cursor.fetchone()
            if result:
                    return {
                        "status": 200, 
                        "message": "Map already existing", 
                        "map-id": int(map_id)
                    }
            
            self.cursor.execute('''
                SELECT * FROM `maps` WHERE `building_name` = ? AND `rooms` = ?
            ''', (str(building_name), int(rooms), ))
            result = self.cursor.fetchone()
            if result:
                    return {
                        "status": 200, 
                        "message": "Map already existing with different map-id",
                        "map-id": int(result[0])
                    }
            
            self.cursor.execute('''
                SELECT * FROM `maps` WHERE `map_id` = ? AND `building_name` <> ?
            ''', (int(map_id), str(building_name), ))
            result = self.cursor.fetchone()
            if result:
                self.cursor.execute('''
                    INSERT INTO maps (building_name, rooms) VALUES (?, ?)
                ''', (building_name, rooms))
                self.conn.commit()
                self.cursor.execute('SELECT * FROM maps WHERE `building_name` = ? AND `rooms` = ? ORDER BY `map_id` DESC', (building_name, rooms, ))
                result = self.cursor.fetchone()
                return {
                    "status": 200, 
                    "message": "Map added with different map-id", 
                    "map-id": result[0]
                }
            else:
                self.cursor.execute('''
                    INSERT INTO maps (map_id, building_name, rooms) VALUES (?, ?, ?)
                ''', (map_id, building_name, rooms))
                self.conn.commit()
                return {"status": 200, "message": "OK", "map-id": map_id}
        except Exception as e:
            return {"status": 400, "error": str(e)}

    
    def add_fingerprint(self, map_id, ssid, bssid, rss_index, frequency, zone, sample):
        try:
            ssid = ssid if (ssid != "") else 'unknown'
            self.cursor.execute('''
                INSERT INTO fingerprints(map_id, ssid, bssid, rss_index, frequency, zone, sample) VALUES (?, ?, ?, ?, ?, ?, ?)
            ''', (map_id, ssid, bssid, rss_index, frequency, zone, sample))
            self.conn.commit()
            return {"status": 200, "message": "OK"}
        except Exception as e:
            return {"status": 400, "error": str(e)}


    def add_direction(self, map_id, zone, sample, azimut, info):
        try:
            self.cursor.execute('''
                INSERT INTO directions(map_id, zone, sample, azimut, info) VALUES (?, ?, ?, ?, ?)
            ''', (map_id, zone, sample, azimut, info))
            self.conn.commit()
            return {"status": 200, "message": "OK"}
        except Exception as e:
            return {"status": 400, "error": str(e)}


    def get_map(self, map_id):
        try: 
            self.cursor.execute('SELECT * FROM maps WHERE map_id = ?',(map_id,))
            map = self.cursor.fetchone()
            self.cursor.execute('''
                SELECT fingerprints.ssid, fingerprints.bssid, fingerprints.frequency, fingerprints.rss_index, fingerprints.zone, fingerprints.sample
                FROM fingerprints INNER JOIN maps ON maps.map_id = fingerprints.map_id 
                WHERE maps.map_id = ?
                ''', (map_id,))
            fingerprints = self.cursor.fetchall()
            return {"status": 200, "map" : map, "fingerprints": fingerprints}
        except Exception as e:
            return {"status": 400, "error": str(e)}

    
    def get_directions(self, map_id):
        try: 
            self.cursor.execute('SELECT * FROM maps WHERE map_id = ?',(map_id,))
            map = self.cursor.fetchone()
            self.cursor.execute('''
                SELECT directions.zone, directions.sample, directions.azimut, directions.info
                FROM directions INNER JOIN maps ON maps.map_id = directions.map_id 
                WHERE maps.map_id = ?
                ''', (map_id,))
            directions = self.cursor.fetchall()
            return {"status": 200, "map" : map, "directions": directions}
        except Exception as e:
            return {"status": 400, "error": str(e)}


    def get_all_maps(self):
        try:
            self.cursor.execute('SELECT building_name AS name, map_id AS id, latitude, longitude FROM maps')
            return {"status": 200, "maps" : self.cursor.fetchall()}
        except Exception as e:
            return {"status": 400, "error": str(e)}


    def close_connection(self):
        self.cursor.close()
        self.conn.close()
