package com.kishan.storagedemo

import android.app.blob.BlobHandle
import android.app.blob.BlobStoreManager
import android.content.Context
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AccessSharedDataSetApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var datasetContent by remember { mutableStateOf("")}
    var errorMessage by remember { mutableStateOf("")}

    LaunchedEffect(Unit) {
        try {
            val blobStoreManager = context.getSystemService(Context.BLOB_STORE_SERVICE) as BlobStoreManager
            val sha256DigestBytes = calculateSha256Digest("Sample photos")
            val blobHandle = BlobHandle.createWithSha256(
                sha256DigestBytes,
                "Sample photos",
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1),
                "photoTrainingDataset"
            )
            val inputStream = ParcelFileDescriptor.AutoCloseInputStream(
                blobStoreManager.openBlob(blobHandle)
            )
            datasetContent = inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            errorMessage = "Error accessing dataset: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        } else {
            Text(text = "Dataset Content:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = datasetContent, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun calculateSha256Digest(data: String): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(data.toByteArray())
}