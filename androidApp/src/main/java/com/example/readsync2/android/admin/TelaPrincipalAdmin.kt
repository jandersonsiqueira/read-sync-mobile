package com.example.readsync

import android.content.Intent
import android.os.Bundle
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

class TelaPrincipalAdmin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminHomeScreen(context = this)
        }
    }
}

@Composable
fun AdminHomeScreen(context: TelaPrincipalAdmin) {
    var searchQuery by remember { mutableStateOf("") }
    var livrosPorGenero by remember { mutableStateOf(mapOf<String, List<Livro2>>()) } // Estado dinâmico

    // Função para buscar livros
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("livros")
            .get()
            .addOnSuccessListener { result ->
                val groupedLivros = result.documents.mapNotNull { doc ->
                    val titulo = doc.getString("titulo")
                    val autor = doc.getString("autor")
                    val genero = doc.getString("genero")
                    if (titulo != null && autor != null && genero != null) {
                        genero to Livro2(titulo, autor)
                    } else {
                        null
                    }
                }.groupBy({ it.first }, { it.second })

                livrosPorGenero = groupedLivros
            }
            .addOnFailureListener { e ->
                println("Erro ao buscar livros: ${e.message}")
            }
    }

    // Interface da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar livros") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                val intent = Intent(context, LivroFormActivity::class.java).apply {
                    putExtra("isEditMode", false)
                }
                context.startActivity(intent)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Adicionar Livro",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            livrosPorGenero.forEach { (genero, livros) ->
                item {
                    Text(
                        text = genero,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(livros) { livro ->
                            LivroCardAdmin(livro = livro, context = context)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun LivroCardAdmin(livro: Livro2, context: TelaPrincipalAdmin) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(300.dp)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = {
                    val intent = Intent(context, LivroFormActivity::class.java).apply {
                        putExtra("isEditMode", true)
                    }
                    context.startActivity(intent)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Editar Livro"
                    )
                }

                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Excluir Livro"
                    )
                }
            }
        }
    }
}

data class Livro2(val titulo: String, val autor: String)

@Preview(showBackground = true)
@Composable
fun DefaultPreviewTelaPrincipalAdmin() {
    AdminHomeScreen(context = TelaPrincipalAdmin())
}