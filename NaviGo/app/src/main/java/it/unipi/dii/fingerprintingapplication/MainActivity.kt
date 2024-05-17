package it.unipi.dii.fingerprintingapplication

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.Toast
import java.util.*

class MainActivity : VolumeNavigation() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_ui)

        // Initialize TextToSpeech
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // Set language to English
                val result = tts.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "English language not supported", Toast.LENGTH_SHORT).show()
                } else {
                    speak("Welcome to NaviGo. Please use the Volume Up button to navigate through the menus and the volume down button to select.")
                }
            } else {
                Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonAdminOptions = findViewById<Button>(R.id.buttonAdminOptions)
        val buttonSelectMap = findViewById<Button>(R.id.buttonSelectMap)

        buttons = listOf(buttonAdminOptions, buttonSelectMap)

        buttonAdminOptions.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }

        buttonSelectMap.setOnClickListener {
            val intent = Intent(this, SelectMapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }

    private fun speak(text: String) {  // Speak the given text using TextToSpeech
        if (::tts.isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}

