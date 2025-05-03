package com.example.bwhite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class cameraGalleryoption : AppCompatActivity() {
    private lateinit var gallerButton : Button
    private lateinit var cameraButton: Button
    private lateinit var recentButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_camera_galleryoption)
        gallerButton = findViewById(R.id.galleryOpenButton)
        cameraButton = findViewById(R.id.liveButton)
        recentButton = findViewById(R.id.recentImagebutton)

        gallerButton.setOnClickListener {
            var intent = Intent(this, galleryPhotoedit::class.java)
            Toast.makeText(this, "Adjust contrast features will be add soon!", Toast.LENGTH_SHORT).show()
            startActivity(intent)


        }

        cameraButton.setOnClickListener {
            var intent  = Intent( this, cameraphotoedit::class.java)
            Toast.makeText(this, "Adjust contrast features will be add soon!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }


    }
}