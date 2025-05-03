package com.example.bwhite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class videoGalleryoption : AppCompatActivity() {
    private lateinit var gallerButton : Button
    private lateinit var cameraButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_galleryoption)
        gallerButton = findViewById(R.id.videogalleryOpenButton)
        cameraButton = findViewById(R.id.vidoeliveButton1)

        gallerButton.setOnClickListener {

            var intent = Intent(this, videoSelection::class.java)
            startActivity(intent)
        }

        cameraButton.setOnClickListener {
            var intent = Intent(this, videoRecording::class.java)
            startActivity(intent)

        }

    }
}
