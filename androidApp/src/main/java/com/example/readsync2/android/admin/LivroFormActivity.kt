package com.example.readsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue

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
    var descricao by remember { mutableStateOf(TextFieldValue("")) }

    val generoOptions = listOf("Terror", "Comédia", "Stand-Alone", "Aventura", "Ficção Científica", "Romance")
    var selectedGenero by remember { mutableStateOf<String?>(null) }

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

        // Exibir Chips de Gêneros
        Text(text = "Gêneros", fontSize = 18.sp)
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(generoOptions) { genero ->
                Chip(
                    selected = selectedGenero == genero,
                    onClick = {
                        selectedGenero = if (selectedGenero == genero) null else genero
                    }
                ) {
                    Text(text = genero)
                }
            }
        }

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
fun Chip(selected: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewLivroForm() {
    LivroFormScreen(isEditMode = false) // Modo de adicionar
}
