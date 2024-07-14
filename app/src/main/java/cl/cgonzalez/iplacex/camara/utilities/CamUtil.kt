package cl.cgonzalez.iplacex.camara.utilities


import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import java.io.File
import java.time.LocalDateTime


fun generarNombreSegunFechaHastaSegundo(): String =
    LocalDateTime.now().toString().replace(Regex("[T:.-]"), "").substring(0, 14)

fun crearArchivoImagenPublico(contexto: Context): File =
    File(
        contexto.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "${generarNombreSegunFechaHastaSegundo()}.jpg"
    )

fun tomarFotografia(
    cameraController: LifecycleCameraController,
    archivo: File,
    contexto: Context,
    nombreUsuario: String,
    onImagenGuardada: (uri: Uri, nombre: String) -> Unit
) {
    val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
    cameraController.takePicture(
        opciones,
        ContextCompat.getMainExecutor(contexto),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = outputFileResults.savedUri
                val nombre = if (nombreUsuario.isNotBlank()) nombreUsuario else generarNombreSegunFechaHastaSegundo()
                if (uri != null) {
                    onImagenGuardada(uri, nombre)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Capturar fotografia", exception.message ?: "Error")
            }
        }
    )
}
