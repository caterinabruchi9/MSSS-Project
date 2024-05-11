import android.widget.Toast
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import android.content.Context



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




class Sample(val zona: Int, val sample: Int, val fingerprints: MutableList<Fingerprint>) {
    fun euclideanDistance(other: Sample, allBssids: Set<String>): Double {
        val rssVector1 = allBssids.map { bssid ->
            fingerprints.find { it.bssid == bssid }?.rss
                ?: -100 // Imposta RSS -100 se BSSID non presente
        }
        val rssVector2 = allBssids.map { bssid ->
            other.fingerprints.find { it.bssid == bssid }?.rss
                ?: -100 // Imposta RSS -100 se BSSID non presente
        }

        // Calcolo della distanza euclidea
        return sqrt(rssVector1.zip(rssVector2).sumOf { (rss1, rss2) ->
            (rss1 - rss2).toDouble().pow(2)
        })
    }

    // Trova il campione più vicino data una lista di campioni e un set completo di BSSID
    fun findNearestSample(samples: List<Sample>, allBssids: Set<String>): Pair<Pair<Int, Int>, Double> {
        var minDistance = Double.MAX_VALUE
        var nearestSample: Pair<Int, Int>? = null

        for (sample in samples) {
            val distance = this.euclideanDistance(sample, allBssids)
            if (distance < minDistance) {
                minDistance = distance
                nearestSample = sample.zona to sample.sample
            }
        }

        return (nearestSample ?: (0 to 0)) to minDistance    }

}
data class Map(val list: List<Sample>)




/*fun main() {
    // Read data from CSV file and create the example map with fingerprints
    val listOfSamples = mutableListOf<Sample>()
    val csvFile = File("map.csv")

    val csvRecords = CSVParser.parse(csvFile, Charsets.UTF_8, CSVFormat.DEFAULT)
    for (record in csvRecords) {
        val ssid = record.get(0)
        val bssid = record.get(1)
        val frequency = record.get(2).toInt()
        val rss = record.get(3).toInt()
        val zona = record.get(4).toInt()
        val sample = record.get(5).toInt()

        val fingerprint = Fingerprint(ssid, bssid, frequency, rss)

        // Check if a sample with the same zone and sample number already exists
        val existingSample = listOfSamples.find { it.zona == zona && it.sample == sample }
        if (existingSample != null) {
            existingSample.fingerprints.add(fingerprint)
        } else {
            val sample = Sample(zona, sample, mutableListOf(fingerprint))
            listOfSamples.add(sample)
        }
    }

    val map = Map(listOfSamples)

    // Choose a random sample from the map
    val originalSample = listOfSamples.random()
    println("Original Sample - Zone: ${originalSample.zona}, Sample: ${originalSample.sample}")
    println("Original RSS Values:")
    originalSample.fingerprints.forEach { fingerprint ->
        println("\tBSSID: ${fingerprint.bssid},\tFrequency: ${fingerprint.frequency},\tRSS: ${fingerprint.rss}")
    }

    // Apply random uniform perturbation to RSS values
    val perturbedFingerprints = originalSample.fingerprints.map { fingerprint ->
        val perturbedRSS = fingerprint.rss + Random.nextInt(-2, 3) // Uniform perturbation between -2 and 2
        fingerprint.rss = perturbedRSS
        fingerprint
    }.toMutableList()

    // Create perturbed sample with same zone and sample value
    val perturbedSample = Sample(originalSample.zona, originalSample.sample, perturbedFingerprints)

    println("\nPerturbed Sample - Zone: ${perturbedSample.zona}, Sample: ${perturbedSample.sample}")
    println("Perturbed RSS Values:")
    perturbedSample.fingerprints.forEach { fingerprint ->
        println("\tBSSID: ${fingerprint.bssid},\tFrequency: ${fingerprint.frequency},\tRSS: ${fingerprint.rss}")
    }

    println("Result: ${perturbedSample.findNearestSample(map.list).toString()}")
}
*/