package com.example.readsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import java.text.SimpleDateFormat
import java.util.*

class LivroFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isEditMode = intent.getBooleanExtra("isEditMode", false)

        setContent {
            LivroFormScreen(isEditMode = isEditMode)
        }
    }
}

@Composable
fun LivroFormScreen(isEditMode: Boolean) {
    var titulo by remember { mutableStateOf(TextFieldValue("")) }
    var autor by remember { mutableStateOf(TextFieldValue("")) }
    var paginas by remember { mutableStateOf(TextFieldValue("")) }
    var dataPublicacao by remember { mutableStateOf(TextFieldValue("")) }
    var urlCapa by remember { mutableStateOf(TextFieldValue("")) }
    var genero by remember { mutableStateOf("Terror") }
    var descricao by remember { mutableStateOf(TextFieldValue("")) }

    val generoOptions = listOf("Terror", "Comédia", "Stand-Alone")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Autor
        OutlinedTextField(
            value = autor,
            onValueChange = { autor = it },
            label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Páginas e Data de Publicação (mesma linha)
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = paginas,
                onValueChange = { paginas = it },
                label = { Text("Páginas") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.3f)
            )

            Spacer(modifier = Modifier.width(15.dp))

            OutlinedTextField(
                value = dataPublicacao,
                onValueChange = { dataPublicacao = it },
                label = { Text("Data de Publicação") },
                modifier = Modifier.weight(0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // URL da capa
        OutlinedTextField(
            value = urlCapa,
            onValueChange = { urlCapa = it },
            label = { Text("URL da Capa") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gênero
        DropdownMenu(
            options = generoOptions,
            selectedOption = genero,
            onOptionSelected = { genero = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Descrição
        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botões
        Row(
            horizontalArrangement = if (isEditMode) Arrangement.SpaceBetween else Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isEditMode) {
                Button(onClick = { /* Ação para Deletar */ }) {
                    Text("Deletar")
                }
            }

            Button(onClick = { /* Ação para Salvar */ }) {
                Text("Salvar")
            }
        }
    }
}

@Composable
fun DropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Text(text = selectedOption)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewLivroForm() {
    LivroFormScreen(isEditMode = false) // Modo de adicionar
}
