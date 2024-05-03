import android.widget.Toast
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import android.content.Context



class Fingerprint(val ssid: String, val bssid: String, val frequency: Int, var rss: Int)


class Sample(val zona: Int, val sample: Int, val fingerprints: MutableList<Fingerprint>){
    fun euclideanDistance(other: Sample): Double {
        val commonFingerprints = mutableListOf<Pair<Fingerprint, Fingerprint>>()

        // Finding common fingerprints based on matching BSSID and frequency
        for (fp1 in this.fingerprints) {
            for (fp2 in other.fingerprints) {
                if (fp1.bssid == fp2.bssid && fp1.frequency == fp2.frequency) {
                    commonFingerprints.add(fp1 to fp2)
                    break
                }
            }
        }

        // Calculating Euclidean distance using common fingerprints
        var sum = 0.0
        for ((fp1, fp2) in commonFingerprints) {
            val diff = fp1.rss - fp2.rss
            sum += diff.toDouble().pow(2)
        }

        return sqrt(sum)
    }

    fun findNearestSample(context: Context, samples: List<Sample>): Pair<Int, Int>? {
        var minDistance = Double.MAX_VALUE
        var nearestSample: Pair<Int, Int>? = null

        for (sample in samples) {
            val distance = this.euclideanDistance(sample)
            if (distance < minDistance) {
                minDistance = distance
                nearestSample = sample.zona to sample.sample
            }
        }

        return nearestSample
    }


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