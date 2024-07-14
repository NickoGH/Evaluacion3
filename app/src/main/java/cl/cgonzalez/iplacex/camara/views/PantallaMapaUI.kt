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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.cgonzalez.iplacex.camara.AppVM
import cl.cgonzalez.iplacex.camara.FormRecepcionViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
fun PantallaMapaUI(
    onVolverClick: () -> Unit,
    appVM: AppVM
) {
    val formRegistroVM: FormRecepcionViewModel = viewModel()
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lat: ${appVM.latitud.value} Long: ${appVM.longitud.value}")

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = {
                MapView(it).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    org.osmdroid.config.Configuration.getInstance().userAgentValue = contexto.packageName
                    controller.setZoom(15.0)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),

            update = {
                it.overlays.removeIf{ true }
                it.invalidate()

                val geoPoint = GeoPoint(appVM.latitud.value, appVM.longitud.value)
                it.controller.animateTo(geoPoint)

                val marcador = Marker(it)
                marcador.position = geoPoint
                marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                it.overlays.add(marcador)

            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
        }) {
            Text("Agregar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onVolverClick()
        }) {
            Text("Volver")
        }
    }
}


