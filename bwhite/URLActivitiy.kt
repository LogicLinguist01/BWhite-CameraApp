package com.example.bwhite
import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.io.File
import java.io.OutputStream


class URLActivitiy : AppCompatActivity() {

    private lateinit var beforeImage: ImageView   //check
    private lateinit var afterImage: ImageView    //
    private lateinit var separator: View          //
    private lateinit var imageViewImage: ImageView //
    private lateinit var seekBar: SeekBar        //
    private lateinit var downloadButton: Button  //
    private lateinit var downloadLink: EditText //
    private lateinit var saveButton: Button

    // Request permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            saveImageToGallery()
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_urlactivitiy)

        beforeImage = findViewById(R.id.beforeimageview)
        afterImage = findViewById(R.id.afterimageview)
        separator = findViewById(R.id.separatorURl)
        imageViewImage = findViewById(R.id.image_view_image)
        seekBar = findViewById(R.id.seekBarURL)
        downloadButton = findViewById(R.id.URLimageDownloadButton)
        downloadLink = findViewById(R.id.Downloadlink1)
        saveButton = findViewById(R.id.image_save)


        // Configure the SeekBar to adjust the processed image overlay.
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val container = findViewById<FrameLayout>(R.id.imageContainerURL)
                container?.let {
                    val containerWidth = it.width
                    val newX = (containerWidth * progress) / 100f
                    // Move the separator.
                    separator.x = newX
                    // Clip the processed (B&W) image dynamically.
                    afterImage.clipBounds = Rect(0, 0, newX.toInt(), afterImage.height)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // No action needed.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // No action needed.
            }
        }
        )

        downloadButton.setOnClickListener {
            // Handle download button click
            Glide.with(this).load(downloadLink.text.toString()).into(beforeImage)
            Glide.with(this).load(downloadLink.text.toString()).into(afterImage)
            seekBar.visibility = View.VISIBLE
            val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
            afterImage.colorFilter = ColorMatrixColorFilter(colorMatrix)

            imageViewImage.visibility = View.GONE


        }

        saveButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // No need for storage permission in Android 10+ (Scoped Storage)
                saveImageToGallery()
            } else {
                // Request permission for Android 9 and below
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    saveImageToGallery()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

        }


    }

    private fun saveImageToGallery() {
        afterImage.isDrawingCacheEnabled = true
        val bitmap: Bitmap = afterImage  .drawingCache

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "SavedImage.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyAppImages")
        }

        val resolver: ContentResolver = contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            imageUri?.let {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
                Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "Failed to Save Image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error Saving Image", Toast.LENGTH_SHORT).show()
        }
}}


