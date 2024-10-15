package com.example.readsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readsync2.android.R

class TelaPrincipalLeitor : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReaderHomeScreen(context = this)
        }
    }
}

@Composable
fun ReaderHomeScreen(context: TelaPrincipalLeitor) {
    var searchQuery by remember { mutableStateOf("") }
    var showFavorites by remember { mutableStateOf(false) }

    val livrosPorGenero = remember {
        mutableStateMapOf(
            "Terror" to listOf(
                Livro("Livro de Terror", "Autor 1", isFavorite = false)
            ),
            "Comédia" to listOf(
                Livro("Livro de Comédia", "Autor 2", isFavorite = false)
            ),
            "Stand-Alone" to listOf(
                Livro("Livro Stand-Alone", "Autor 3", isFavorite = false)
            )
        )
    }

    val livrosFiltrados = if (showFavorites) {
        livrosPorGenero.mapValues { entry ->
            entry.value.filter { it.isFavorite }
        }.filterValues { it.isNotEmpty() }
    } else {
        livrosPorGenero
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barra de pesquisa
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar livros") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = showFavorites,
                onCheckedChange = { showFavorites = it }
            )
            Text(text = "Favoritos", modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            livrosFiltrados.forEach { (genero, livros) ->
                item {
                    Text(
                        text = genero,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(livros) { livro ->
                    LivroCard(livro = livro) { updatedBook ->
                        val updatedList = livrosPorGenero[genero]?.map {
                            if (it.titulo == updatedBook.titulo) updatedBook else it
                        } ?: listOf()
                        livrosPorGenero[genero] = updatedList
                    }
                }
            }
        }
    }
}

@Composable
fun LivroCard(livro: Livro, onFavoriteClick: (Livro) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_book),
                contentDescription = "Capa do livro",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = livro.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = livro.autor,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = painterResource(
                    id = if (livro.isFavorite) R.drawable.ic_favorite_outline else R.drawable.ic_favorite_filled
                ),
                contentDescription = "Favoritar livro",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onFavoriteClick(livro.copy(isFavorite = !livro.isFavorite))
                    }
            )
        }
    }
}

data class Livro(val titulo: String, val autor: String, var isFavorite: Boolean)

@Preview(showBackground = true)
@Composable
fun DefaultPreviewTelaPrincipalLeitor() {
    ReaderHomeScreen(context = TelaPrincipalLeitor())
}