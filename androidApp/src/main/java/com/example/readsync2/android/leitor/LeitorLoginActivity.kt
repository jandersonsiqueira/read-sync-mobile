package com.example.readsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.readsync2.android.R

class LeitorLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(context = this)
        }
    }
}

@Composable
fun LoginScreen(context: LeitorLoginActivity) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Login Leitor",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botão "Cadastre-se"
        Text(
            text = "Cadastre-se",
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .clickable {
                    val intent = Intent(context, CadastroActivity::class.java)
                    startActivity(context, intent, null)
                }
                .padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botão "Login com Google"
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Login com Google",
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    // TODO: Implementar o login com Google
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // TODO: Validar email e senha
                // Se as credenciais forem válidas, navegue para TelaPrincipalLeitor

                val intent = Intent(context, TelaPrincipalLeitor::class.java)
                startActivity(context, intent, null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewLeitorLoginActivity() {
    LoginScreen(context = LeitorLoginActivity())
}