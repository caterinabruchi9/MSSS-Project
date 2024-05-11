import sqlite3
import pandas as pd

def read_csv(file_path):
    # Read the CSV file into a DataFrame
    df = pd.read_csv(file_path, sep=',')
    return df


# Function to insert data into the maps table
def insert_map(map_id, building_name, rooms, latitude, longitude, conn, cursor):
    cursor.execute('''
        INSERT INTO maps (building_name, rooms, latitude, longitude) VALUES (?, ?, ?, ?)
    ''', (building_name, rooms, latitude, longitude))
    conn.commit()


# Function to insert data into the directions table
def insert_direction(map_id, zone, sample, azimut, info, conn, cursor):
    cursor.execute('''
        INSERT INTO directions(map_id, zone, sample, azimut, info) VALUES (?, ?, ?, ?, ?)
    ''', (map_id, zone, sample, azimut, info))
    conn.commit()


# Function to insert data into the fingerprint table
def insert_fingerprint(map_id, ssid, bssid, rss_index, frequency, zone, sample, conn, cursor):
    ssid = ssid if not pd.isna(ssid) else 'unknown'
    cursor.execute('''
        INSERT INTO fingerprints(map_id, ssid, bssid, rss_index, frequency, zone, sample) VALUES (?, ?, ?, ?, ?, ?, ?)
    ''', (map_id, ssid, bssid, rss_index, frequency, zone, sample))
    conn.commit()


def insert_fingerprints(file_path, map_id, conn, cursor):
    df = read_csv(file_path)

    for index, row in df.iterrows():
        insert_fingerprint(map_id, row['ssid'], row['bssid'], row['rss_index'], row['frequency'], row['zone'], row['sample'], conn, cursor)


def insert_directions(file_path, map_id, conn, cursor):
    df = read_csv(file_path)

    for index, row in df.iterrows():
        insert_direction(map_id, row['zone'], row['sample'], row['azimut'], row['info'],conn, cursor)



# Function to retrieve data from the maps table
def get_maps(cursor):
    cursor.execute('SELECT * FROM maps')
    return cursor.fetchall()


def main():
    # Connect to the SQLite database (create one if it doesn't exist)
    conn = sqlite3.connect('db.sqlite')

    # Create a cursor object to execute SQL queries
    cursor = conn.cursor()

    # Create the maps table if it doesn't exist
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS maps (
            map_id INTEGER PRIMARY KEY AUTOINCREMENT,
            building_name TEXT,
            rooms INTEGER,
            latitude REAL, 
            longitude REAL
        )
    ''')
    conn.commit()
    
    insert_map(1, "Scuola di Ingegneria - Polo A - 2nd floor", 22, 43.7213889, 10.3898333, conn, cursor)
    insert_map(2, "[1] Casa Marco", 3, 44.0786362, 10.0254536, conn, cursor)
    insert_map(3, "[2] Casa Marco", 3, 44.0786362, 10.0254536, conn, cursor)

    # Create the fingerprints table if it doesn't exist
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS fingerprints (
            map_id INTEGER NOT NULL,
            ssid TEXT,
            bssid TEXT NOT NULL,
            rss_index INTEGER NOT NULL,
            frequency INTEGER NOT NULL,
            zone INTEGER NOT NULL,
            sample INTEGER NOT NULL,
            PRIMARY KEY (map_id, bssid, frequency, zone, sample)
        )
     ''')
    conn.commit()
    
    insert_fingerprints('wi-fi_fingerprints.2.1.csv', 1, conn, cursor)

    # Create the directions table if it doesn't exist
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS directions (
            map_id INTEGER NOT NULL,
            zone INTEGER NOT NULL,
            sample INTEGER NOT NULL,
            azimut REAL NOT NULL,
            info TEXT NOT NULL,
            PRIMARY KEY (map_id, zone, sample, azimut),
            FOREIGN KEY (map_id) REFERENCES maps(map_id)
        )
    ''')
    conn.commit()

    insert_directions('directions.csv', 1, conn, cursor)

    # Close the cursor and connection when done
    cursor.close()
    conn.close()



if __name__ == '__main__':
    main()
