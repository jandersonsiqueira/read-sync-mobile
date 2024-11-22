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
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var dataPublicacao by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var paginas by remember { mutableStateOf(TextFieldValue("")) }
    val db = FirebaseFirestore.getInstance()

    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

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
                        capaUrl = doc.getString("urlCapa") ?: ""
                        descricao = TextFieldValue(doc.getString("descricao") ?: "")
                        titulo = doc.getString("titulo") ?: ""
                        autor = doc.getString("autor") ?: ""
                        dataPublicacao = doc.getString("dataPublicacao") ?: ""
                        genero = doc.getString("genero") ?: ""
                        paginas = TextFieldValue((doc["paginas"] as Long).toString())
                    } else {
                        println("Documento não encontrado.")
                    }
                }
                .addOnFailureListener { e ->
                    println("Erro ao buscar detalhes do livro: ${e.message}")
                }

            db.collection("comentarios")
                .whereEqualTo("livroId", livroId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val comentarios = snapshot.documents.mapNotNull { document ->
                        document.getString("comentario")
                    }
                    listaComentarios = comentarios
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
            // Botão de voltar
            Button(
                onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Voltar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Capa do livro
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = capaUrl,
                    contentDescription = "Capa do livro",
                    modifier = Modifier.size(150.dp),
                    placeholder = painterResource(id = R.drawable.ic_book),
                    error = painterResource(id = R.drawable.ic_book)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Informações do livro
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Título: $titulo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        tts?.speak("Título: $titulo", TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Ler título",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Autor: $autor",
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        tts?.speak("Autor: $autor", TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Ler autor",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Data de Publicação: $dataPublicacao",
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        tts?.speak("Data de Publicação: $dataPublicacao", TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Ler data de publicação",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Gênero: $genero",
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        tts?.speak("Gênero: $genero", TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Ler gênero",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Páginas: ${paginas.text}",
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        tts?.speak("Numero de Páginas: ${paginas.text}", TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Ler páginas",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Descrição: ${descricao.text}",
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        tts?.speak("Descrição: ${descricao.text}", TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Ler descrição",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comentários
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
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(text = "Ainda não há comentários para este livro.", modifier = Modifier.padding(8.dp))
            }
        }

        // Campo de adicionar comentário
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
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
                    val comentarioData = hashMapOf(
                        "livroId" to livroId,
                        "comentario" to comentario
                    )
                    db.collection("comentarios")
                        .add(comentarioData)
                        .addOnSuccessListener {
                            listaComentarios = listaComentarios + comentario
                            comentario = ""
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