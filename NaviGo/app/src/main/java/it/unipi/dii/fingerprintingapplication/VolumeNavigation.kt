package it.unipi.dii.fingerprintingapplication

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import java.util.*

// Base class for volume-based navigation with Text-to-Speech
open class VolumeNavigation : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var selectedButtonIndex = 0
    protected lateinit var buttons: List<Button> // Accessible to subclasses
    private lateinit var tts: TextToSpeech // Text-to-Speech instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this) // Initialize Text-to-Speech
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                selectCurrentButton()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {

                navigateUp()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }



    private fun navigateUp() {
        if (::buttons.isInitialized && buttons.isNotEmpty()) {
            selectedButtonIndex = if (selectedButtonIndex - 1 < 0) {
                buttons.size - 1
            } else {
                selectedButtonIndex - 1
            }
            selectButton(selectedButtonIndex) // Select and announce the button
        }
    }



    private fun selectButton(index: Int) {
        buttons.forEachIndexed { i, button ->
            if (i == index) {
                button.setBackgroundResource(android.R.color.holo_blue_light)
                speak(button.text.toString()) // Speak the button's text when selected
            } else {
                button.setBackgroundResource(android.R.color.transparent)
            }
        }

        Toast.makeText(this, "Selected button ${index + 1}", Toast.LENGTH_SHORT).show()
    }


    private fun selectCurrentButton() {
        val selectedButtonText = buttons[selectedButtonIndex].text.toString()
        speak(selectedButtonText) // Speak the button's text
        buttons[selectedButtonIndex].performClick() // Perform the click event on the selected button
    }


    private fun speak(text: String) {
        if (::tts.isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) // Speak the text
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US) // Set the desired language
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.shutdown() // Properly shut down TTS
        }
    }
}