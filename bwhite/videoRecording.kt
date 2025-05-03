package com.example.bwhite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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


class videoRecording : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var recordButton: Button

    companion object {
        private const val REQUEST_VIDEO_CAPTURE = 2
        private const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_recording)

        imageView = findViewById(R.id.imageView)
        recordButton = findViewById(R.id.vidbutton1)

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV Initialization Failed!")
        } else {
            Log.d("OpenCV", "OpenCV Initialized Successfully")
        }

        // Request camera & storage permissions
        requestPermissions()

        recordButton.setOnClickListener {
            Log.d("DEBUG", "Button Clicked!")
            val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10) // Limit to 10 seconds
            startActivityForResult(videoIntent, REQUEST_VIDEO_CAPTURE)
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = data?.data
            if (videoUri != null) {
                Log.d("DEBUG", "Video recorded: $videoUri")
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
