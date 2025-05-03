package com.example.bwhite

import androidx.appcompat.app.AppCompatActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class videoSelection : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectVideoButton: Button

    companion object {
        private const val REQUEST_VIDEO_PICK = 1
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_selection)

        imageView = findViewById(R.id.imageView)
        selectVideoButton = findViewById(R.id.vidbutton1)

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV Initialization Failed!")
        } else {
            Log.d("OpenCV", "OpenCV Initialized Successfully")
        }

        // Request necessary permissions
        requestPermissions()

        selectVideoButton.setOnClickListener {
            selectVideoFromGallery()
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun selectVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_VIDEO_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_PICK && resultCode == Activity.RESULT_OK) {
            val videoUri: Uri? = data?.data
            if (videoUri != null) {
                Log.d("DEBUG", "Video selected: $videoUri")
                processVideo(videoUri)
            }
        }
    }

    private fun processVideo(videoUri: Uri) {
        Thread {
            val videoPath = getRealPathFromURI(videoUri) ?: run {
                Log.e("OpenCV", "Failed to retrieve video path")
                return@Thread
            }

            val videoCapture = VideoCapture(videoPath)
            if (!videoCapture.isOpened) {
                Log.e("OpenCV", "Failed to open video")
                return@Thread
            }

            val frame = Mat()
            val grayFrame = Mat()

            while (videoCapture.read(frame)) {
                if (frame.empty()) break

                // Rotate frame if needed (Adjust based on testing)
                val rotatedFrame = Mat()
                Core.rotate(frame, rotatedFrame, Core.ROTATE_90_CLOCKWISE)

                // Convert to grayscale
                Imgproc.cvtColor(rotatedFrame, grayFrame, Imgproc.COLOR_BGR2GRAY)
                Imgproc.resize(grayFrame, grayFrame, Size(400.0, 600.0)) // Resize to match ImageView

                // Convert Mat to Bitmap
                val bitmap = matToBitmap(grayFrame)

                // Update UI on the main thread
                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }

                Thread.sleep(33) // ~30 FPS
                rotatedFrame.release()
            }

            videoCapture.release()
        }.start()
    }

    private fun matToBitmap(mat: Mat): Bitmap {
        val bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, bmp)
        return bmp
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val fileName = "temp_video.mp4"
        val file = File(cacheDir, fileName)
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(contentUri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            Log.e("DEBUG", "Failed to copy file: ${e.message}")
            null
        }
    }

}