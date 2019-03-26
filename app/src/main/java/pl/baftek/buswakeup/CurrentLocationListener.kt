package pl.baftek.buswakeup

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
class CurrentLocationListener private constructor(appContext: Context) : LiveData<Location>() {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
    val locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setPriority(PRIORITY_HIGH_ACCURACY)

    init {
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
                value = result?.lastLocation
            }
        }, null)
    }

    companion object {
        var instance: CurrentLocationListener? = null

        fun getInstance(appContext: Context): CurrentLocationListener {
            if (instance == null) instance = CurrentLocationListener(appContext)
            return instance as CurrentLocationListener
        }
    }
}