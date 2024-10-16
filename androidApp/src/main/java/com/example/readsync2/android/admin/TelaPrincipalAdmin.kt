package com.example.readsync

import android.content.Intent
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

    val livrosPorGenero = remember {
        mutableStateMapOf(
            "Terror" to listOf(
                Livro2("Livro de Terror", "Autor 1")
            ),
            "Comédia" to listOf(
                Livro2("Livro de Comédia", "Autor 2")
            ),
            "Stand-Alone" to listOf(
                Livro2("Livro Stand-Alone", "Autor 3")
            )
        )
    }

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
                }
                items(livros) { livro ->
                    LivroCardAdmin(livro = livro, context = context)
                }
            }
        }
    }
}

@Composable
fun LivroCardAdmin(livro: Livro2, context: TelaPrincipalAdmin) {
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

data class Livro2(val titulo: String, val autor: String)

@Preview(showBackground = true)
@Composable
fun DefaultPreviewTelaPrincipalAdmin() {
    AdminHomeScreen(context = TelaPrincipalAdmin())
}