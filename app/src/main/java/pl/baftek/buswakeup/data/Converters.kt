package pl.baftek.buswakeup.data

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class Converters {
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        val lat = latLng.latitude
        val lng = latLng.longitude

        return "$lat,$lng"
    }

    @TypeConverter
    fun toLatLng(str: String): LatLng {
        val pieces = str.split(",")

        val lat = pieces[0]
        val lng = pieces[1]

        return LatLng(lat.toDouble(), lng.toDouble())
    }
}