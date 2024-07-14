package cl.cgonzalez.iplacex.camara.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.cgonzalez.iplacex.camara.CameraAppViewModel
import cl.cgonzalez.iplacex.camara.FormRecepcionViewModel
import cl.cgonzalez.iplacex.camara.Pantalla

//Pantallla Principal
@Composable
fun PantallaFormUI(
    formRecepcionVm: FormRecepcionViewModel,
    cameraAppViewModel: CameraAppViewModel
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            cameraAppViewModel.pantalla.value = Pantalla.CAMARA
        },
            modifier = Modifier
                .padding(16.dp)
                .height(80.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Tomar Foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            cameraAppViewModel.pantalla.value = Pantalla.FOTOS
        },modifier = Modifier
            .padding(16.dp)
            .height(80.dp)
            .fillMaxWidth()
        ) {
            Text(text = "Ver Fotos")
        }
    }
}
