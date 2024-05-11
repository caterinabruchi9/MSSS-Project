package it.unipi.dii.fingerprintingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : VolumeNavigation() { // Extend VolumeNavigationActivity to use volume-based navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_ui)

        val buttonAdminOptions = findViewById<Button>(R.id.buttonAdminOptions)
        val buttonSelectMap = findViewById<Button>(R.id.buttonSelectMap)

        buttons = listOf(buttonAdminOptions, buttonSelectMap) // Define buttons for navigation

        buttonAdminOptions.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java) // Open the admin activity
            startActivity(intent)
        }

        buttonSelectMap.setOnClickListener {
            val intent = Intent(this, SelectMapActivity::class.java) // Open the map selection activity
            startActivity(intent)
        }
    }
}