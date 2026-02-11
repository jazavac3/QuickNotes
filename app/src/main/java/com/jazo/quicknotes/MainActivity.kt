package com.jazo.quicknotes

// imports for date formatting
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jazo.quicknotes.ui.theme.QuickNotesTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.background

import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign


@Composable
fun NoteScreen()  {
    val viewModel : NoteViewModel = viewModel()
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    var showSettings by remember { mutableStateOf(false)}
    var noteText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false)}


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp).fillMaxWidth()

        )    {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xB41E88E5), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Q",
                        color = Color.White,
                        fontSize = 24.sp,  // keep 24, otherwise too big for the box which wraps it
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Cursive
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "QuickNotes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }


            IconButton(onClick = {showSettings = true}) {      // TODO: add settings menu
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            DropdownMenu(expanded = showSettings, onDismissRequest = {showSettings = false}) {

                DropdownMenuItem(text = { Text("Dark Mode") }, onClick = {showSettings = false})       // Says dark mode but doesn't do anything :(

                DropdownMenuItem(text = {Text("Clear all Notes")}, onClick = {
                    showSettings = false
                    notes.forEach { viewModel.delete(it) }
                })

                DropdownMenuItem(text = {Text("About")}, onClick = {
                    showSettings = false
                    showDialog = true
                })
            }


        }

        Divider()


        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = noteText,
            onValueChange = {noteText = it},
            placeholder = { Text("Enter your note here")},
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF3A3A3A),  // Background when typing
                unfocusedContainerColor = Color(0xFF3A3A3A),  // Background when not typing
                focusedTextColor = Color.White,  // Text color when typing
                unfocusedTextColor = Color.White,  // Text color when not typing
                cursorColor = Color.White,  // Blinking cursor
                focusedIndicatorColor = Color(0xB41E88E5),  // Bottom line when focused
                unfocusedIndicatorColor = Color.Gray,  // Bottom line when not focused
                focusedPlaceholderColor = Color.Gray,  // Placeholder when typing
                unfocusedPlaceholderColor = Color.Gray  // Placeholder when not typing
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (noteText.isNotBlank()) {
                    viewModel.insert(Note(content = noteText))
                    noteText = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Note", color = Color.White)

        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(32.dp))

        if (notes.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                Text("No notes yet!", color = Color.White, fontSize = 32.sp, modifier = Modifier.fillMaxWidth().padding(top = 64.dp), textAlign = TextAlign.Center)
            }

        } else {
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(notes) { note ->
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date(note.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF3A3A3A)  // Slightly lighter than background
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)){
                                Text(text = note.content, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = date,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            IconButton(onClick = { viewModel.delete(note) }) {
                                Text("×", fontSize = 24.sp, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF3A3A3A),  // Dark background
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Blue, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Q",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "QuickNotes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            },
            text = {
                Column {
                    Text("Version 1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("A fast and simple notes app built with Kotlin and Jetpack Compose.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Developed by jazavac")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "QN")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xB41E88E5),
                    onPrimary = Color.White,
                    background = Color(0xFF2B2B2B),
                    onBackground = Color.White,
                    surface = Color(0xFF3A3A3A),
                    onSurface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2B2B2B)
                ) {
                    NoteScreen()
                }
            }
        }
    }
}



