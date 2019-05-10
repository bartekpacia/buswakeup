package pl.baftek.buswakeup.dsl

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Returns a distance from target [LatLng] in kilometers
 */
fun Location.distanceFrom(target: LatLng): Float {
    val results = FloatArray(1)
    Location.distanceBetween(this.latitude, this.longitude, target.latitude, target.longitude, results)

    return results[0] / 1000
}