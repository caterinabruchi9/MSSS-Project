package it.unipi.dii.fingerprintingapplication



import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import it.unipi.dii.fingerprintingapplication.R

class CompassActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var magnetometer: Sensor? = null
    private var accelerometer: Sensor? = null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private lateinit var buttonGetAzimuth: Button
    private lateinit var textViewAzimuth: TextView

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        buttonGetAzimuth = findViewById<Button>(R.id.orientationButton)
        textViewAzimuth = findViewById(R.id.orientationResult)

        buttonGetAzimuth.setOnClickListener {
            // Trigger azimuth calculation when the button is clicked
            startAzimuthSampling()
        }
    }

    private fun startAzimuthSampling() {
        buttonGetAzimuth.isEnabled = false // Disable the button during sampling

        // Register sensor listeners
        magnetometer?.also { magSensor ->
            sensorManager.registerListener(
                this,
                magSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        accelerometer?.also { accelSensor ->
            sensorManager.registerListener(
                this,
                accelSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Stop sampling after one second
        handler.postDelayed({
            stopAzimuthSampling()
        }, 1000)
    }

    private fun stopAzimuthSampling() {
        sensorManager.unregisterListener(this) // Unregister sensor listeners
        buttonGetAzimuth.isEnabled = true // Re-enable the button after sampling
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something if accuracy changes
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        updateOrientationAngles()
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // Convert azimuth from radians to degrees
        val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()

        // Ensure azimuthDegrees is between 0 and 360
        val correctedAzimuth = if (azimuthDegrees < 0) azimuthDegrees + 360 else azimuthDegrees

        // Update TextView with the corrected azimuth
        textViewAzimuth.text = "Azimuth: $correctedAzimuth"
    }


    private fun calculateOrientation(reference: Float, measured: Float){

        if (measured > (reference+315)%360 && measured < (reference+45)%360)
            println("Sei dritto rispetto alla direzione di riferimento")
        else if (measured < (reference+225)%360 && measured > (reference+135)%360)
            println("Sei al contrario rispetto alla direzione di riferimento")
        else if (measured < (reference+135)%360 && measured > (reference+45)%360)
            println("Sei girato verso destra")
        else println("Sei girato verso sinistra")


    }
}
