import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import it.unipi.dii.fingerprintingapplication.R

class CompassActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var magnetometer: Sensor? = null

    private lateinit var textViewCompass: TextView
    private lateinit var buttonCompass: Button

    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        textViewCompass = findViewById(R.id.orientationResult)
        buttonCompass = findViewById(R.id.orientationButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        buttonCompass.setOnClickListener {
            if (!isListening) {
                startListening()
                buttonCompass.text = "Stop"
            } else {
                stopListening()
                buttonCompass.text = "Start"
            }
            isListening = !isListening
        }
    }

    override fun onResume() {
        super.onResume()
        if (isListening) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        stopListening()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something if accuracy changes
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val azimuth = calculateAzimuth(event.values[0], event.values[1])
            updateUI(azimuth)
            stopListening()
        }
    }

    private fun calculateAzimuth(x: Float, y: Float): Float {
        return Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble())).toFloat()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(azimuth: Float) {
        textViewCompass.text = "Azimuth: $azimuth°"
    }

    private fun startListening() {
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopListening() {
        sensorManager.unregisterListener(this)
    }
}
