package cl.cgonzalez.iplacex.camara.views

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import cl.cgonzalez.iplacex.camara.AppVM
import cl.cgonzalez.iplacex.camara.CameraAppViewModel
import cl.cgonzalez.iplacex.camara.FormRecepcionViewModel
import cl.cgonzalez.iplacex.camara.Pantalla
import cl.cgonzalez.iplacex.camara.utilities.conseguirUbicacion


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGuardarUI(
    formRecepcionVm: FormRecepcionViewModel,
    cameraAppViewModel: CameraAppViewModel,
    appVM: AppVM,
    PermisosGPS:ActivityResultLauncher<Array<String>>,
    onVolverClick: () -> Unit
) {
    var imageName by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        formRecepcionVm.fotos.lastOrNull()?.let { (uri, nombre) ->
            Image(
                painter = rememberImagePainter(data = uri),
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = imageName.takeIf { it.isNotBlank() } ?: nombre)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = imageName,
            onValueChange = { imageName = it },
            label = { Text("Nombre de la foto") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            appVM.permisoUbicacionOK = {
                conseguirUbicacion(contexto) { ubicacion ->
                    appVM.latitud.value = ubicacion.latitude
                    appVM.longitud.value = ubicacion.longitude
                    // Cambiar la pantalla a Pantalla.MAPA
                    cameraAppViewModel.pantalla.value = Pantalla.MAPA
                }
            }

            PermisosGPS.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }) {
            Text("Ubicación")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (imageName.isNotBlank()) {
                val (uri, _) = formRecepcionVm.fotos.lastOrNull() ?: return@Button
                formRecepcionVm.fotos.add(Pair(uri, imageName))
                imageName = ""
                cameraAppViewModel.pantalla.value = Pantalla.CAMARA
            }
        }) {
            Text("Guardar y Tomar Otra")
        }
    }
}






