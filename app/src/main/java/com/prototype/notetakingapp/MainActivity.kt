package com.prototype.notetakingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.prototype.notetakingapp.ui.theme.NoteTakingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteTakingAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = remember { MainViewModel() }) {
    val context = LocalContext.current
    var fetchedText by remember { mutableStateOf("Loading...") }

    var notes by remember { mutableStateOf(NoteStorage.getAllNotes(context)) }
    var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }

    // Fetch data when the screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchTextFromApi { fetchedText = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // **Fetched Text Displayed at Top**
        Text(
            text = fetchedText,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // **Text Input Fields for Notes**
        Text(text = "Title:", style = MaterialTheme.typography.titleMedium)
        BasicTextField(
            value = noteTitle,
            onValueChange = { noteTitle = it },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Content:", style = MaterialTheme.typography.titleMedium)
        BasicTextField(
            value = noteContent,
            onValueChange = { noteContent = it },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // **Buttons for Save & Delete**
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (noteTitle.text.isNotEmpty() && noteContent.text.isNotEmpty()) {
                    NoteStorage.saveNote(context, noteTitle.text, noteContent.text)
                    notes = NoteStorage.getAllNotes(context)
                    noteTitle = TextFieldValue("")
                    noteContent = TextFieldValue("")
                }
            }) { Text(text = "Save") }

            Button(onClick = {
                NoteStorage.deleteLatestNote(context)
                notes = NoteStorage.getAllNotes(context)
            }) { Text(text = "Delete Latest") }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // **Always Show Saved Notes at the Bottom with Indexing**
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Saved Notes:", style = MaterialTheme.typography.headlineSmall)

            if (notes.isEmpty()) {
                Text(text = "No notes saved.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Box(modifier = Modifier.padding(8.dp)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        itemsIndexed(notes.entries.toList().reversed()) { index, (title, content) ->
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "${index + 1}. $title",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(text = content, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}