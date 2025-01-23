package com.kishan.storagedemo

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.kishan.storagedemo.models.Video
import java.util.concurrent.TimeUnit

//Method for fetching Video from External storage Gallery
fun fetchVideosFromGallery(context: Context): List<Video>{
    val videoList = mutableListOf<Video>()

    val collection =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
    )

    val selection = "${MediaStore.Video.Media.DURATION} >= ?"
    val selectionArgs = arrayOf(
        TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES).toString()
    )

    val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

    val query = context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

        while(cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getLong(nameColumn)
            val duration = cursor.getInt(durationColumn)
            val size = cursor.getInt(sizeColumn)

            val contentUri : Uri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id
            )

            videoList += Video(contentUri, name.toString(), duration, size)
        }
    }

    return videoList
}

@RequiresApi(Build.VERSION_CODES.Q)
fun loadThumbnail(context:Context, uri:Uri): Bitmap {
    return context.contentResolver.loadThumbnail(uri, Size(640, 480), null)
}

//Method for fetching Images from External storage Gallery
fun fetchImagesFromGallery(context: Context):List<Uri> {
    val imageList = mutableListOf<Uri>()
    val contentResolver = context.contentResolver

    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Images.Media._ID)

    val cursor = contentResolver.query(
        uri,
        projection,
        null,
        null,
        "${MediaStore.Images.Media.DATE_ADDED} DESC"
    )

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val contentUri = Uri.withAppendedPath(uri, id.toString())
            imageList.add(contentUri)
        }
    }

    return imageList
}

@Composable
fun ImageGalleryScreen(imageUris: List<Uri>, modifier: Modifier = Modifier   ) {
    LazyColumn {
        items(imageUris){uri ->
            ImageItem(imageUri = uri)
        }
    }
}

@Composable
fun ImageItem(imageUri: Uri) {
    Image(painter = rememberAsyncImagePainter(imageUri), contentDescription = null, modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(8.dp),contentScale = ContentScale.Crop)
}

//Video Gallery Ui
@Composable
fun VideoGalleryScreen(videoList:List<Video>, loadThumbnail: (context: Context, uri: Uri) -> Bitmap,  modifier: Modifier = Modifier) {
    LazyColumn {
        items(videoList){ video ->
            VideoItem(video = video, loadThumbnail = loadThumbnail)
        }
    }
}

@Composable
fun VideoItem(video:Video, loadThumbnail: (context: Context, uri: Uri) -> Bitmap, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        val thumbnail = loadThumbnail(LocalContext.current, video.uri)
        Image(
            bitmap = thumbnail.asImageBitmap(),
            contentDescription = null,
            modifier = modifier
                .size(100.dp)
                .padding(end = 8.dp)
        )
        Column {
            Text(text = video.name, maxLines = 1)
            Text(text = "Duration : ${video.duration / 1000}s")
            Text(text = "Size : ${video.size / 1024}KB")
        }
    }
}