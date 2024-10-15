package com.example.readsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelaInicial(context = this)
        }
    }
}

@Composable
fun TelaInicial(context: MainActivity) { // Adicione o parâmetro 'context'
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Você é?",
            fontSize = 24.sp
        )

        Button(
            onClick = {
                val intent = Intent(context, LeitorLoginActivity::class.java) // Use 'context'
                intent.putExtra("tipoUsuario", "leitor")
                startActivity(context, intent, null) // Use 'context' para startActivity
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Leitor")
        }

        Button(
            onClick = {
                val intent = Intent(context, AdminLoginActivity::class.java)
                intent.putExtra("tipoUsuario", "admin")
                startActivity(context, intent, null) // Use 'context' para startActivity
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Administrador")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewMainActivity() {
    TelaInicial(context = MainActivity()) // Passe o contexto para a preview
}