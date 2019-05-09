package pl.baftek.buswakeup.dsl

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun Location.distanceFrom(target: LatLng): Int {
    val results = FloatArray(1)
    Location.distanceBetween(this.latitude, this.longitude, target.latitude, target.longitude, results)

    return results[0].toInt()
}