package com.example.bwhite

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class galleryPhotoedit : AppCompatActivity() {

    private lateinit var imageView: ImageView  // Before image
    private lateinit var frameImageView: ImageView  // After image
    private lateinit var separator: View  // Vertical slider line
    private lateinit var seekBar: SeekBar
    private lateinit var button: Button
    private val pickImageRequest = 1000
    private lateinit var add_view : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gallery_photoedit)

        imageView = findViewById(R.id.imageView)
        frameImageView = findViewById(R.id.frameImageView)
        separator = findViewById(R.id.separator)
        seekBar = findViewById(R.id.seekBar)
        button = findViewById(R.id.selectImageButton)
        add_view = findViewById(R.id.addimages)

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            add_view.visibility = View.GONE
            startActivityForResult(intent, pickImageRequest)
        }
        add_view.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            add_view.visibility = View.GONE
            startActivityForResult(intent, pickImageRequest)
        }

        // SeekBar Listener for Before-After Effect
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val parentWidth = findViewById<FrameLayout>(R.id.imageContainer).width
                val newX = (parentWidth * progress) / 100f

                // Move the separator
                separator.x = newX

                // Clip the after-image dynamically
                frameImageView.clipBounds = android.graphics.Rect(0, 0, newX.toInt(), frameImageView.height)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                imageView.setImageURI(selectedImageUri)  // Load original image
                frameImageView.setImageURI(selectedImageUri)  // Load duplicate image

                // Apply grayscale filter to the "After" image
                val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
                frameImageView.colorFilter = ColorMatrixColorFilter(colorMatrix)

                seekBar.visibility = View.VISIBLE
            }
        }
        else{
            add_view.visibility = View.VISIBLE
        }

    }
}