package com.example.practica1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practica1.ui.theme.Practica1Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica1Theme {
                ImcScreen()
            }
        }
    }
}

fun calcularIMC(weight: String, height: String): Double? {
    val weightValue = weight.toDoubleOrNull()
    val heightValue = height.toDoubleOrNull()

    return if (weightValue != null && heightValue != null && heightValue > 0) {
        weightValue / (heightValue * heightValue)
    } else {
        null
    }
}

fun obtenerCategoria(imc: Double?): String {
    return when {
        imc == null -> "Datos inválidos"
        imc < 18.5 -> "Bajo peso"
        imc < 25.0 -> "Normal"
        imc < 30.0 -> "Sobrepeso"
        else -> "Obesidad"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImcScreen() {

    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Calculadora IMC") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            BotonCalcularImc {
                val imc = calcularIMC(weight, height)
                val categoria = obtenerCategoria(imc)

                resultado = if (imc != null) {
                    """
                    Nombre: $name
                    IMC: %.2f
                    Categoría: $categoria
                    """.format(imc).trimIndent()
                } else {
                    "Datos inválidos"
                }

                scope.launch {
                    snackbarHostState.showSnackbar("Cálculo realizado")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
        ) {

            FormularioImc(
                name = name,
                weight = weight,
                height = height,
                onNameChange = { name = it },
                onWeightChange = { weight = it },
                onHeightChange = { height = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ResultadoImc(resultado)
        }
    }
}
@Composable
fun FormularioImc(
    name: String,
    weight: String,
    height: String,
    onNameChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit
) {
    Column {

        AppTextField(
            value = name,
            onValueChange = onNameChange,
            label = "Nombre",
            placeholder = "Escribe tu nombre"
        )

        AppTextField(
            value = weight,
            onValueChange = onWeightChange,
            label = "Peso (kg)",
            placeholder = "Ej: 70"
        )

        AppTextField(
            value = height,
            onValueChange = onHeightChange,
            label = "Estatura (m)",
            placeholder = "Ej: 1.75"
        )
    }
}

@Composable
fun ResultadoImc(resultado: String) {
    if (resultado.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = resultado,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun BotonCalcularImc(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Calcular IMC"
        )
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}