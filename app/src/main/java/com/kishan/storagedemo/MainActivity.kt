package com.kishan.storagedemo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.kishan.storagedemo.models.Video
import com.kishan.storagedemo.ui.theme.StorageDemoTheme

class MainActivity : ComponentActivity() {
    private var imageUris by mutableStateOf<List<Uri>>(emptyList())
    private var videoList by mutableStateOf<List<Video>>(emptyList())

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        enableEdgeToEdge()


        setContent {
            StorageDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    ImageGalleryScreen(imageUris = imageUris, modifier = Modifier.padding(innerPadding))
//                    VideoGalleryScreen(videoList = videoList, loadThumbnail = ::loadThumbnail, modifier = Modifier.padding(innerPadding))
//                    ImageAndVideoPickingScreen(modifier = Modifier.padding(innerPadding))
//                    AccessDocumentAndOtherFilesApp(modifier = Modifier.padding(innerPadding))
                    SharedPreferenceDemoApp(modifier = Modifier.padding(innerPadding))
                }

            }
        }
    }

    private fun requestPermissions() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if(isGranted) {
//                imageUris = fetchImagesFromGallery(this)
                videoList = fetchVideosFromGallery(this)
            }
        }

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
//                imageUris = fetchImagesFromGallery(this)
                videoList = fetchVideosFromGallery(this)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}
