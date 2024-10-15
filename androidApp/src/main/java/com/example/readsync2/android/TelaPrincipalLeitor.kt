package com.example.readsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.readsync2.android.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Olá, Leitor!",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Exibe um único livro com imagem
        LivroCard(titulo = "Título do Livro", autor = "Autor")

        Spacer(modifier = Modifier.weight(1f)) // Espaço vazio para o botão ficar na parte inferior

        Button(
            onClick = {
                // TODO: Implementar a lógica para carregar livros da API
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar Livros")
        }
    }
}

@Composable
fun LivroCard(titulo: String, autor: String) {
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
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = autor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewTelaPrincipalLeitor() {
    ReaderHomeScreen(context = TelaPrincipalLeitor())
}