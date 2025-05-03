package com.example.bwhite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var button: Button
    private lateinit var textView: TextView
    private lateinit var user: FirebaseUser
    private lateinit var photoOpt: Button
    private lateinit var videobutton :Button
    private lateinit var externallink : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        button = findViewById(R.id.Logout_button)
        textView = findViewById(R.id.main_text_view)
        photoOpt = findViewById(R.id.photoButton)
        videobutton = findViewById(R.id.videoMain)
        externallink = findViewById(R.id.external_link_button)
        val user = auth.currentUser

        if ( user == null){
            val intent = Intent( this, login_page::class.java)
            startActivity(intent)
            finish()

        }
        else{
            textView.text = user.email
        }

        button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, login_page::class.java)
            startActivity(intent)
            finish()
        }
        photoOpt.setOnClickListener {
            val intent = Intent(this, cameraGalleryoption::class.java)
            startActivity(intent)
        }
        videobutton.setOnClickListener {
            val intent = Intent(this , videoGalleryoption::class.java)
            startActivity(intent)
        }
        externallink.setOnClickListener {
            val intent = Intent( this, URLActivitiy::class.java)
            startActivity(intent)
        }

        }
    }
