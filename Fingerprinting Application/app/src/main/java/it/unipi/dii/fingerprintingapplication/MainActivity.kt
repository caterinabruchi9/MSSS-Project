package it.unipi.dii.fingerprintingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonCreateNewMap = findViewById<Button>(R.id.buttonCreateNewMap)
        buttonCreateNewMap.setOnClickListener {


            // Intent per avviare ScanActivity in modalità "Crea Nuova Mappa"
            val intent = Intent(this, ScanActivity::class.java)
            intent.putExtra("mode", "new")
            startActivity(intent)
        }

        val buttonUseExistingMap = findViewById<Button>(R.id.buttonUseExistingMap)
        buttonUseExistingMap.setOnClickListener {
            // Qui puoi aggiungere la logica per mostrare le mappe esistenti e selezionarne una
            // Ad esempio, mostrare un dialogo o una nuova Activity con la lista delle mappe
        }
    }
}
