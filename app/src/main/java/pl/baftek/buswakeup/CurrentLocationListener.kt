package pl.baftek.buswakeup

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
class CurrentLocationListener private constructor(appContext: Context) : LiveData<Location>() {
    private val TAG = "LocationListener"

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
    private val locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(3500)
            .setPriority(PRIORITY_HIGH_ACCURACY)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(userLocation: LocationResult?) {
            Log.d(TAG, "locationCallback ${userLocation?.lastLocation.toString()}")
            value = userLocation?.lastLocation
        }
    }

    override fun onActive() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        Log.d(TAG, "onActive")
    }

    override fun onInactive() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "onInactive")
    }

    companion object {
        private var instance: CurrentLocationListener? = null

        fun getInstance(appContext: Context): CurrentLocationListener {
            if (instance == null) instance = CurrentLocationListener(appContext)
            return instance as CurrentLocationListener
        }
    }
}