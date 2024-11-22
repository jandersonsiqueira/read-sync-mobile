package com.example.readsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TelaChatbot : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatbotScreen()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatbotScreen() {
    var userInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<String>()) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Exibe o histórico do chat
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(chatHistory) { message ->
                Text(
                    text = message,
                    fontSize = 16.sp,
                    fontWeight = if (message.startsWith("Chatbot:")) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row para incluir o botão dentro do campo de entrada
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)  // Espaço entre os elementos
        ) {
            // Campo para entrada do usuário
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Digite sua mensagem") },
                modifier = Modifier.weight(1f)  // Preenche o espaço disponível
            )

            // Botão de enviar mensagem
            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        // Simula uma resposta automática do chatbot
                        val response = "Resposta automática: $userInput"
                        chatHistory = chatHistory + "Você: $userInput" + "Chatbot: $response"
                        userInput = ""

                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)  // Alinha o botão ao centro verticalmente
            ) {
                Text("Enviar")
            }
        }
    }
}
