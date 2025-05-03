package com.example.bwhite

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class Registration : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonReg: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressBar
    private lateinit var textview: TextView

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

            auth = FirebaseAuth.getInstance()
            editTextEmail = findViewById(R.id.email)
            editTextPassword = findViewById(R.id.password)
            buttonReg = findViewById(R.id.button_registration)
            progress = findViewById(R.id.progressbar)
            textview= findViewById(R.id.LoginNow)

        textview.setOnClickListener {
            val intent = Intent(this, login_page::class.java) // Use 'this' for context
            startActivity(intent)
            finish()
        }

        buttonReg.setOnClickListener {
            progress.visibility = View.VISIBLE
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@Registration, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@Registration, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progress.visibility = View.VISIBLE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        Toast.makeText(
                            baseContext,
                            "Authentication Successful.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(this, login_page::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        }
        }
    }
