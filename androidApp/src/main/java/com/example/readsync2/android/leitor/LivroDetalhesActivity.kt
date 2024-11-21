package com.example.readsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readsync2.android.R
import com.google.firebase.firestore.FirebaseFirestore

class LivroDetalhesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val livroId = intent.getStringExtra("livroId")
        setContent {
            LivroDetalhesScreen(livroId = livroId)
        }
    }
}

@Composable
fun LivroDetalhesScreen(livroId: String?) {
    var descricao by remember { mutableStateOf(TextFieldValue("")) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(livroId) {
        if (livroId != null) {
            // Acessando o documento corretamente
            db.collection("livros").document(livroId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        // Garantir que o campo 'descricao' existe e é do tipo String
                        val descricaoCampo = doc.getString("descricao")
                        if (descricaoCampo != null) {
                            descricao = TextFieldValue(descricaoCampo)
                        } else {
                            println("Descrição não encontrada ou campo não é uma string.")
                        }
                    } else {
                        println("Documento não encontrado.")
                    }
                }
                .addOnFailureListener { e ->
                    println("Erro ao buscar detalhes do livro: ${e.message}")
                }
        } else {
            println("ID do livro está vazio.")
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Capa do livro
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_book),
                contentDescription = "Capa do livro",
                modifier = Modifier.size(128.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Descrição: ${descricao.text}",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Seção visual de áudio (não funcional por enquanto)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            IconButton(onClick = { /* Simular play/pause */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = "Play",
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(text = "00:00 / 03:45", fontSize = 14.sp)

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = { /* Simular pausa */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentDescription = "Pause",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        var comentario by remember { mutableStateOf("") }
        var listaComentarios by remember { mutableStateOf(listOf<String>()) }

        // Exibe os comentários
        LazyColumn {
            items(listaComentarios) { comentario ->
                Text(text = comentario, modifier = Modifier.padding(8.dp))
            }
        }

        // Seção para adicionar comentários
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Escreva seu comentário") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                if (comentario.isNotEmpty()) {
                    listaComentarios = listaComentarios + comentario
                    comentario = ""
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Enviar",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}