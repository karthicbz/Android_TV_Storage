package com.example.tekionapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.Manifest
import android.os.Environment
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : FragmentActivity() {
    private lateinit var downloadAudio:Button
    private lateinit var downloadVideo:Button
    private lateinit var inputStream: InputStream
    private lateinit var audioFileText:TextView
    private lateinit var videoFileText:TextView


    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadAudio = findViewById(R.id.download_audio)
        downloadVideo = findViewById(R.id.download_video)
        audioFileText = findViewById(R.id.audio_file_text)
        videoFileText = findViewById(R.id.video_file_text)

        if(!checkAndRequestPermissions()){
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }

        downloadAudio.setOnClickListener {
//            Toast.makeText(this, "Clicked download audio", Toast.LENGTH_SHORT).show()
            saveFileToExternalStorage("audio")
        }

        downloadVideo.setOnClickListener{
//            Toast.makeText(this, "Clicked download video", Toast.LENGTH_SHORT).show()
            saveFileToExternalStorage("video")
        }

        displayFilePath()
    }

    private fun checkAndRequestPermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val listPermissionsNeeded = mutableListOf<String>()

        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                val perms = mutableMapOf<String, Int>()
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        perms[permissions[i]] = grantResults[i]
                    }

                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED &&
                        perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Some permissions are denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveFileToExternalStorage(fileType:String) {

        try {
            if(fileType == "audio") {
                inputStream =
                    resources.openRawResource(R.raw.music_file) // Replace 'your_audio_file' with the actual file name
            }else{
                inputStream = resources.openRawResource(R.raw.video_file)
            }
            val externalPath = File(
                //if(fileType == "audio") Environment.DIRECTORY_MUSIC else Environment.DIRECTORY_MOVIES
                getExternalFilesDir(null),
                "ott/assets/${if(fileType == "audio")"audio" else "video"}"
            )

            if (!externalPath.exists()) {
                externalPath.mkdirs()
            }

            val outFile = File(externalPath, if(fileType == "audio")"audio_file.mp3" else "video_file.mp4") // Replace 'audio_file.mp3' with the desired file name
            val outputStream = FileOutputStream(outFile)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.close()
            inputStream.close()

            Toast.makeText(this, "${if(fileType == "audio")"Audio" else "Video"} saved to ${outFile.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save audio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayFilePath() {
        val externalAudioPath = File(
            getExternalFilesDir(null),
            "ott/assets/audio/audio_file.mp3"
        ) // Replace 'audio_file.mp3' with the desired file name

        val externalVideoPath = File(getExternalFilesDir(null),
            "ott/assets/video/video_file.mp4"
        )

        if (externalAudioPath.exists()) {
            audioFileText.text = "Audio file: ${externalAudioPath.absolutePath}"
        } else {
            audioFileText.text = "No audio file"
        }

        if(externalVideoPath.exists()){
            videoFileText.text = "Video file: ${externalVideoPath.absolutePath}"
        }else{
            videoFileText.text = "No video file"
        }
    }
}