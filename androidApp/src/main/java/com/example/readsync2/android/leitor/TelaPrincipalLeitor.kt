package com.example.readsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.google.firebase.firestore.FirebaseFirestore

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
    var livrosPorGenero by remember { mutableStateOf(mapOf<String, List<Livro>>()) }

    // Buscar livros do Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("livros")
            .get()
            .addOnSuccessListener { result ->
                val groupedLivros = result.documents.mapNotNull { doc ->
                    val titulo = doc.getString("titulo")
                    val autor = doc.getString("autor")
                    val genero = doc.getString("genero")
                    val isFavorite = doc.getBoolean("isFavorite") ?: false
                    if (titulo != null && autor != null && genero != null) {
                        genero to Livro(titulo, autor, isFavorite)
                    } else {
                        null
                    }
                }.groupBy({ it.first }, { it.second })

                livrosPorGenero = groupedLivros
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar livros: ${e.message}")
            }
    }

    // Filtro de favoritos
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
                item {
                    LazyRow {
                        items(livros) { livro ->
                            LivroCard(
                                livro = livro,
                                onFavoriteClick = { updatedBook ->
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("livros")
                                        .whereEqualTo("titulo", updatedBook.titulo)
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            querySnapshot.documents.forEach { doc ->
                                                db.collection("livros").document(doc.id)
                                                    .update("isFavorite", updatedBook.isFavorite)
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Erro ao atualizar favorito: ${e.message}")
                                        }

                                    val updatedList = livrosPorGenero[genero]?.map {
                                        if (it.titulo == updatedBook.titulo) updatedBook else it
                                    } ?: listOf()
                                    livrosPorGenero = livrosPorGenero.toMutableMap().apply {
                                        this[genero] = updatedList
                                    }
                                },
                                onBookClick = {
                                    Log.d("LivroCard", "Livro clicado: ${livro.titulo}")
                                    val intent = Intent(context, LivroDetalhesActivity::class.java)
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LivroCard(livro: Livro, onFavoriteClick: (Livro) -> Unit, onBookClick: (Livro) -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(300.dp)
            .clickable { onBookClick(livro) }
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_book),
                contentDescription = "Capa do livro",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.weight(1f))

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