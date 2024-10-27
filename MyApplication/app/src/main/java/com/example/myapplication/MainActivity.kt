package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                LayoutMain()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutMain() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var usuariosNome by remember { mutableStateOf("") }
    var usuariosCpf by remember { mutableStateOf("") }
    var usuariosSaldo by remember { mutableStateOf("") }
    var usuariosList by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var mensagem by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(text = "Gerenciamento de Usuários", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = usuariosNome,
            onValueChange = { usuariosNome = it },
            label = { Text("Nome do Usuário") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = usuariosCpf,
            onValueChange = { usuariosCpf = it },
            label = { Text("CPF do Usuário") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = usuariosSaldo,
            onValueChange = { usuariosSaldo = it },
            label = { Text("Saldo Inicial") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val saldo = usuariosSaldo.toDoubleOrNull() ?: 0.0
            val novoUsuario = Usuario(nome = usuariosNome, cpf = usuariosCpf, saldo = saldo)

            CoroutineScope(Dispatchers.IO).launch {
                db.usuarioDAO().insertUsuario(novoUsuario)
                mensagem = "Usuário $usuariosNome adicionado!"
            }

            usuariosNome = ""
            usuariosCpf = ""
            usuariosSaldo = ""
        }) {
            Text("Salvar Usuário")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Listar usuários
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                usuariosList = db.usuarioDAO().getAllUsuarios()
            }
        }) {
            Text("Listar Usuários")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = mensagem)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(usuariosList) { usuario ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${usuario.nome} - Saldo: ${usuario.saldo}")

                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                db.usuarioDAO().deleteUsuario(usuario.id)
                                usuariosList = db.usuarioDAO().getAllUsuarios() // Atualizar lista após deletar
                            }
                        }
                    ) {
                        Text("Deletar")
                    }
                }
            }
        }
    }
}
