from math import sqrt
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.colors as mcolors

def read_csv(file_path):
    df = pd.read_csv(file_path, sep=',')
    return df

class Sample:
    def __init__(self, fingerprints):
        self.fingerprints = fingerprints
    
    def euclidean_distance(self, other):
        self_rss    = self.fingerprints
        self_rss.sort(key=lambda x: x[0])  # Sort the self_rss list
        self_bssid  = [item[0] for item in self_rss]
        
        other_rss   = other.fingerprints
        other_rss.sort(key=lambda x: x[0])  # Sort the other_rss list
        other_bssid = [item[0] for item in other_rss]

        for bssid in self_bssid:
            if bssid not in other_bssid:
                other_rss.append((bssid, -100))

        for bssid in other_bssid:
            if bssid not in self_bssid:
                self_rss.append((bssid, -100))

        rss_vector1 = [rss for _, rss in self_rss]
        rss_vector2 = [rss for _, rss in other_rss]

        # euclidean distance
        return sqrt(sum((rss1 - rss2) ** 2 for rss1, rss2 in zip(rss_vector1, rss_vector2)))

def calculate_euclidean_distances(zones):
    num_zones = len(zones)
    euclidean_distances = np.zeros((num_zones, num_zones), dtype=np.float64)

    for i in range(num_zones):
        for j in range(i + 1, num_zones):
            zone1 = Sample(zones[i])
            zone2 = Sample(zones[j])
            distance = zone1.euclidean_distance(zone2)
            euclidean_distances[i, j] = distance
            euclidean_distances[j, i] = distance

    return euclidean_distances

def plot_heatmap(all_zones, total_samples):
    euclidean_distances = calculate_euclidean_distances(all_zones)
    cmap = plt.cm.viridis

    plt.figure(figsize=(12, 10))
    plt.imshow(euclidean_distances, cmap=cmap, interpolation='nearest', origin='upper',
               extent=[0, total_samples, total_samples, 0])
    plt.colorbar(label='Euclidean Distance')
    plt.xlabel('Sample')
    plt.ylabel('Sample')
    plt.title('Euclidean Distances Between Subregions of All Zones')
    plt.gca().invert_yaxis()  # Reverse the y-axis
    plt.grid(True)
    plt.savefig('euclidean_distances_heatmap_all_zones.png')

def main():
    df = read_csv('wi-fi_fingerprints.1.1.csv')

    all_zones_data = []
    total_samples = 0

    for zone_number in range(1, 12):
        filtered_df = df[df['zone'] == zone_number]
        max_sample = filtered_df['sample'].max()
        zone_data = [[] for _ in range(max_sample)]

        for _, row in filtered_df.iterrows():
            zone_data[int(row['sample']) - 1].append((row['bssid'], row['rss_index']))

        all_zones_data.extend(zone_data)
        total_samples += max_sample

    plot_heatmap(all_zones_data, total_samples)

if __name__ == '__main__':
    main()
