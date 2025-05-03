package com.example.bwhite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class cameraphotoedit : AppCompatActivity() {

    private lateinit var originalImage: ImageView     // Original (color) image (at bottom)
    private lateinit var processedImage: ImageView    // Processed (B&W) image (overlay)
    private lateinit var separator: View              // Vertical slider line
    private lateinit var slider: SeekBar              // SeekBar to adjust before-after split
    private lateinit var clickPic: Button             // Button to capture image
    private lateinit var backgroud_button : ImageView
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cameraphotoedit)

        // Check for CAMERA and RECORD_AUDIO permissions and request if not granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                1
            )
        }

        // Initialize views (make sure these IDs match those in your XML layout)
        originalImage = findViewById(R.id.imageViewlive)
        processedImage = findViewById(R.id.frameImageViewlive)
        separator = findViewById(R.id.separatorlive)
        slider = findViewById(R.id.seekBarlive)
        clickPic = findViewById(R.id.clickimageButtonlive)
        backgroud_button = findViewById(R.id.image_view_button)

        // Launch the camera when the button is clicked.
        clickPic.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 1)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            backgroud_button.visibility = View.GONE
        }

        backgroud_button.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 1)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            backgroud_button.visibility = View.GONE
        }

        // Configure the SeekBar to adjust the processe d image overlay.
        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val container = findViewById<FrameLayout>(R.id.imageContainer)
                container?.let {
                    val containerWidth = it.width
                    val newX = (containerWidth * progress) / 100f
                    // Move the separator.
                    separator.x = newX
                    // Clip the processed (B&W) image dynamically.
                    processedImage.clipBounds = Rect(0, 0, newX.toInt(), processedImage.height)
                }
            }


            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // No action needed.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // No action needed.
            }


        })
    }

    // Handle the result from the camera capture intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                // Set the original (color) image.
                originalImage.setImageBitmap(bitmap)
                // Set the processed image (to be converted to grayscale) in the overlay.
                processedImage.setImageBitmap(bitmap)
                // Apply a grayscale filter to the processed image.
                val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
                processedImage.colorFilter = ColorMatrixColorFilter(colorMatrix)
                // Make the SeekBar visible.
                slider.visibility = View.VISIBLE
            }
        }
        else{
            backgroud_button.visibility = View.VISIBLE
        }
    }
}
