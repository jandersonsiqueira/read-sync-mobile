package com.example.readsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
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
import coil.compose.rememberAsyncImagePainter
import com.example.readsync2.android.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

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
    var capaUrl by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var listaComentarios by remember { mutableStateOf(listOf<String>()) }
    val db = FirebaseFirestore.getInstance()

    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    // Inicializando o TextToSpeech
    LaunchedEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = tts?.setLanguage(Locale("pt", "BR"))
                if (langResult != TextToSpeech.LANG_AVAILABLE) {
                    Log.e("TTS", "Idioma não disponível.")
                }
            } else {
                Log.e("TTS", "Falha na inicialização do TTS.")
            }
        }
    }

    LaunchedEffect(livroId) {
        if (livroId != null) {
            // Acessando o documento do livro
            db.collection("livros").document(livroId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        // Carregar a URL da capa
                        capaUrl = doc.getString("urlCapa") ?: ""
                        descricao = TextFieldValue(doc.getString("descricao") ?: "")
                    } else {
                        println("Documento não encontrado.")
                    }
                }
                .addOnFailureListener { e ->
                    println("Erro ao buscar detalhes do livro: ${e.message}")
                }

            // Carregar os comentários do Firestore
            db.collection("comentarios")
                .whereEqualTo("livroId", livroId) // Filtra pelos comentários do livro específico
                .get()
                .addOnSuccessListener { snapshot ->
                    // Mapeia os comentários do Firestore para uma lista
                    val comentarios = snapshot.documents.mapNotNull { document ->
                        document.getString("comentario")
                    }
                    listaComentarios = comentarios // Atualiza a lista local com os comentários
                }
                .addOnFailureListener { e ->
                    println("Erro ao carregar os comentários: ${e.message}")
                }
        } else {
            println("ID do livro está vazio.")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                AsyncImage(
                    model = capaUrl,
                    contentDescription = "Capa do livro",
                    modifier = Modifier.size(250.dp),
                    placeholder = painterResource(id = R.drawable.ic_book),
                    error = painterResource(id = R.drawable.ic_book)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descrição do livro com botão de áudio
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Descrição: ${descricao.text}",
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    tts?.speak(descricao.text, TextToSpeech.QUEUE_FLUSH, null, null)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Ler descrição",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seção de comentários com botão de áudio
            if (listaComentarios.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp)
                ) {
                    items(listaComentarios) { comentario ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = comentario, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                tts?.speak(comentario, TextToSpeech.QUEUE_FLUSH, null, null)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_play),
                                    contentDescription = "Ler comentário",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Exibe uma mensagem caso não haja comentários
                Text(text = "Ainda não há comentários para este livro.", modifier = Modifier.padding(8.dp))
            }
        }

        // Seção fixa para adicionar comentários
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter) // Fixa no fundo da tela
        ) {
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Escreva seu comentário") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                if (comentario.isNotEmpty() && livroId != null) {
                    // Salvar o comentário no Firestore
                    val comentarioData = hashMapOf(
                        "livroId" to livroId,
                        "comentario" to comentario
                    )

                    db.collection("comentarios")
                        .add(comentarioData)
                        .addOnSuccessListener {
                            // Adicionando o comentário à lista local
                            listaComentarios = listaComentarios + comentario
                            comentario = "" // Limpar o campo de comentário
                        }
                        .addOnFailureListener { e ->
                            println("Erro ao adicionar comentário: ${e.message}")
                        }
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Enviar",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}