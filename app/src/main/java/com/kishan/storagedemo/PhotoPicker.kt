package com.kishan.storagedemo

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Preview(showBackground = true)
@Composable
fun ImageAndVideoPickingScreen(modifier: Modifier = Modifier) {
    val selectImageUri = remember{ mutableStateOf<Uri?>(null)}
    val selectImageUriList = remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if(uri != null) {
                selectImageUri.value = uri
                Log.d("ImageUri", "$uri")
            }
            else {
                selectImageUri.value = null
                Log.d("ImageUri", "No media selected")
            }

        }
    )

    val pickMultipleImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5),
        onResult = { uris ->
            selectImageUriList.value = uris ?: emptyList()
        }
    )

    Box(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)){
        Column(modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text(text = "Pick Single Image")
            }
            Box (modifier = modifier
                .size(290.dp)
                .border(
                    width = 4.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ), contentAlignment = Alignment.Center){
                Image(
                    modifier = modifier.size(290.dp),
                    painter = rememberAsyncImagePainter(
                        model = selectImageUri.value,
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Button(onClick = {
                pickMultipleImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }, modifier = modifier.padding(top = 16.dp)) {
                Text(text = "Pick Multiple Images")
            }
            Spacer(modifier = modifier.padding(16.dp))
            Box (modifier = modifier
                .size(290.dp)
                .border(
                    width = 4.dp,
                    color = Color.LightGray,
                ),
            ){
                LazyRow {
                    items(selectImageUriList.value) {imageUri ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = imageUri
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = modifier.size(100.dp).padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}


