package com.kishan.storagedemo

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

@Preview
@Composable
fun SharedPreferenceDemoApp(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    var newValue by remember { mutableStateOf("") }
    var readValue by remember { mutableStateOf("") }


    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        TextField(
            value = newValue ,
            onValueChange = {
                newValue = it
            },
            placeholder = {
                Text(text = "Enter New Value")
            }
        )
        Spacer(modifier = modifier.height(16.dp))
        Button(
            onClick = {
                with(sharedPreferences.edit()) {
                    putString("NEW_VALUE", newValue)
                    apply()
                }
                Toast.makeText(context, "Value Saved", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text(text = "Save")
        }

        Spacer(modifier = modifier.height(16.dp))

        Button(onClick = {
            readValue = sharedPreferences.getString("NEW_VALUE", "") ?: ""
        }) {
            Text(text = "Show Value")
        }

        Text(text = "Value: $readValue", style = MaterialTheme.typography.bodyLarge)
    }

}