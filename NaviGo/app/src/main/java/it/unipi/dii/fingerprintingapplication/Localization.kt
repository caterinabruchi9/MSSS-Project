
//Distance calculation methods and classes

import kotlin.math.pow
import kotlin.math.sqrt

class Fingerprint(
    var ssid: String = "unknown",
    val bssid: String,
    val frequency: Int,
    var rss: Int
) {
    init {
        if (this.ssid.isBlank()) this.ssid = "unknown"
    }
}




class Sample(val zone: Int, val sample: Int, val fingerprints: MutableList<Fingerprint>) {
    fun euclideanDistance(other: Sample, allBssids: Set<String>): Double {
        val rssVector1 = allBssids.map { bssid ->
            fingerprints.find { it.bssid == bssid }?.rss
                ?: -100 // Uses allBss to ensure the same dimensionality and fill missing values with -100
        }
        val rssVector2 = allBssids.map { bssid ->
            other.fingerprints.find { it.bssid == bssid }?.rss
                ?: -100 // Uses allBss to ensure the same dimensionality and fill missing values with -100
        }

        // euclidean distance
        return sqrt(rssVector1.zip(rssVector2).sumOf { (rss1, rss2) ->
            (rss1 - rss2).toDouble().pow(2)
        })
    }

    // Find the nearest sample to the current sample
    fun findNearestSample(samples: List<Sample>, allBSSIDs: Set<String>): Pair<Pair<Int, Int>, Double> {
        var minDistance = Double.MAX_VALUE
        var nearestSample: Pair<Int, Int>? = null

        for (sample in samples) {
            val distance = this.euclideanDistance(sample, allBSSIDs)
            if (distance < minDistance) {
                minDistance = distance
                nearestSample = sample.zone to sample.sample
            }
        }

        return (nearestSample ?: (0 to 0)) to minDistance    }

}
