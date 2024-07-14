package cl.cgonzalez.iplacex.camara

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import cl.cgonzalez.iplacex.camara.views.PantallaCamaraUI
import cl.cgonzalez.iplacex.camara.views.PantallaFormUI
import cl.cgonzalez.iplacex.camara.views.PantallaFotosUI
import cl.cgonzalez.iplacex.camara.views.PantallaGuardarUI
import cl.cgonzalez.iplacex.camara.views.PantallaMapaUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


enum class Pantalla {
    FORM,
    CAMARA,
    FOTOS,
    GUARDAR,
    MAPA
}

class CameraAppViewModel : ViewModel() {
    val pantalla = mutableStateOf(Pantalla.FORM)

}
class FormRecepcionViewModel : ViewModel() {
    val fotos = mutableStateListOf<Pair<Uri?, String>>()
}
class AppVM : ViewModel(){
    val latitud = mutableStateOf(0.0)
    val longitud = mutableStateOf(0.0)
    var permisoUbicacionOK : () -> Unit = {}
}


class MainActivity  : ComponentActivity() {
    val cameraAppVm : CameraAppViewModel by viewModels()
    val appVM       : AppVM by viewModels()

    private val formRecepcionVm: FormRecepcionViewModel by viewModels()

    private lateinit var cameraController: LifecycleCameraController
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestCameraPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Log.e("Permisos", "Permiso uso de camara denegado")
            }
        }

    val PermisosGPS = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (
            (it[Manifest.permission.ACCESS_FINE_LOCATION] ?: false) or
            (it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false)
        ) {
            appVM.permisoUbicacionOK()
        }else{
            Log.e("PermisosGPS callback","Permisos Denegados")
        }
    }

    private fun requestCameraPermission() {
        when {
            isCameraPermissionGranted() -> {
                openCamera()
            }
            else -> {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                } else {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }
    }

    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }



    private fun openCamera() {
        setContent {
            AppUI(
                cameraController,
                formRecepcionVm,
                cameraAppVm,
                appVM,
                ::requestCameraPermission,
                ::isCameraPermissionGranted,
                PermisosGPS

            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCamera()
        requestCameraPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupCamera() {
        cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    }
}

@Composable
fun AppUI(
    cameraController: LifecycleCameraController,
    formRecepcionVm: FormRecepcionViewModel,
    cameraAppViewModel: CameraAppViewModel,
    appVM: AppVM,
    requestCameraPermission: () -> Unit,
    isCameraPermissionGranted: () -> Boolean,
    PermisosGPS: ActivityResultLauncher<Array<String>>
) {
    MaterialTheme {
        Surface {
            when (val currentScreen = cameraAppViewModel.pantalla.value) {
                Pantalla.FORM -> {
                    PantallaFormUI(formRecepcionVm, cameraAppViewModel)
                }

                Pantalla.CAMARA -> {
                    PantallaCamaraUI(
                        cameraController,
                        cameraAppViewModel,
                        formRecepcionVm,
                        requestCameraPermission,
                        isCameraPermissionGranted,
                        onImagenGuardada = { uri, nombre ->
                            formRecepcionVm.fotos.add(Pair(uri, nombre))
                            cameraAppViewModel.pantalla.value = Pantalla.GUARDAR
                        }
                    )
                }

                Pantalla.FOTOS -> {
                    PantallaFotosUI(
                        formRecepcionVm,
                        cameraAppViewModel,
                        onTomarFotoClick = {
                            cameraAppViewModel.pantalla.value = Pantalla.CAMARA
                        }
                    )
                }

                Pantalla.GUARDAR -> {
                    PantallaGuardarUI(
                        formRecepcionVm,
                        cameraAppViewModel,
                        appVM,
                        PermisosGPS,
                        onVolverClick = {

                            cameraAppViewModel.pantalla.value = Pantalla.FOTOS
                        }
                    )
                }

                Pantalla.MAPA -> {
                    PantallaMapaUI(
                        onVolverClick = {
                            cameraAppViewModel.pantalla.value = Pantalla.GUARDAR
                        },
                        appVM = appVM
                    )
                }
            }
        }
    }
}


