package cl.cgonzalez.iplacex.camara.utilities

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class FaltaPermisosExeption(mensaje:String): Exception(mensaje)

fun conseguirUbicacion(contexto: Context, onSuccess:(ubicacion:Location) -> Unit){
    try {
        val servicio    = LocationServices.getFusedLocationProviderClient(contexto)
        val tarea       = servicio.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        )
        tarea.addOnSuccessListener{
            onSuccess(it)
        }
    } catch (se: SecurityException){
        throw FaltaPermisosExeption("Sin Permisos")
    }

}









