package com.example.readsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class AdminLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminLoginScreen(context = this)
        }
    }
}

fun validateAccessCode(context: AdminLoginActivity, inputCode: String, onSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("adminCodes").document("adminAccessCode")

    docRef.get().addOnSuccessListener { document ->
        if (document != null) {
            val storedCode = document.getString("codigo")
            if (storedCode == inputCode) {
                onSuccess()
            } else {
                Toast.makeText(context, "Código de acesso incorreto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Documento não encontrado", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Erro ao acessar o Firestore", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun AdminLoginScreen(context: AdminLoginActivity) {
    var codigoAcesso by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Login Administrador",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = codigoAcesso,
            onValueChange = { codigoAcesso = it },
            label = { Text("Código de Acesso") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                validateAccessCode(context, codigoAcesso) {
                    val intent = Intent(context, TelaPrincipalAdmin::class.java)
                    startActivity(context, intent, null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewAdminLoginActivity() {
    AdminLoginScreen(context = AdminLoginActivity())
}