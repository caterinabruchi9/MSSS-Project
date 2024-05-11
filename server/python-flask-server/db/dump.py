import sqlite3
import sys
import csv

def dump_db(table: str=None, file: str=None):

    if table is None:
        print("table must be not \'None\'")
        sys.exit(1)

    if file is None:
        print("file must be not \'None\'")
        sys.exit(1)

    conn = sqlite3.connect('db.sqlite')
    cursor = conn.cursor()


    cursor.execute(f"SELECT * FROM {table}")
    rows = cursor.fetchall()

    csv_file_path = f'{file}.csv'
    with open(csv_file_path, 'w', newline='') as csvfile:
        csv_writer = csv.writer(csvfile)
        
        # Write the header
        csv_writer.writerow([i[0] for i in cursor.description])
        
        # Write the data
        csv_writer.writerows(rows)

    cursor.close()
    conn.close()

    print(f"{csv_file_path} file created successfully!")


def main():
    dump_db(sys.argv[1], sys.argv[2])



if __name__ == '__main__':
    main()
