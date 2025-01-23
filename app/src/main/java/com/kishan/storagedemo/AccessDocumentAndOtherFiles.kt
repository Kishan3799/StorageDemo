package com.kishan.storagedemo

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.InputStreamReader

@Preview(showBackground = true)
@Composable
fun AccessDocumentAndOtherFilesApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var fileContent by remember {
        mutableStateOf("")
    }

    val openDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {uri ->
            selectedUri = uri
            uri?.let {
                fileContent = readFileContent(context,uri)
            }
        }
    )

    val createDocLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("application/pdf"),
        onResult = {resultUri ->
            selectedUri = resultUri
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(
                state = rememberScrollState()
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Button(onClick = {
            openDocLauncher.launch(arrayOf("text/plain", "application/pdf", ))
        }) {
            Text(text = "Open Document")
        }
        
        Spacer(modifier = modifier.height(16.dp))

        Button(onClick = {
            createDocLauncher.launch("invoice.pdf")
        }) {
            Text(text = "Create Pdf file")
        }

        Spacer(modifier = modifier.height(16.dp))

        selectedUri?.let {
            Text(text = "Selected Uri: $it", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = modifier.height(16.dp))

        if(fileContent.isNotEmpty()) {
            Text(text = "File Content: ", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = modifier.height(8.dp))
            Text(text = fileContent, style = MaterialTheme.typography.bodySmall)
        }


    }
}

private fun readFileContent(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return "Unable to Open file"
    val bufferReader = BufferedReader(InputStreamReader(inputStream))
    return bufferReader.use { it.readText() }
}
