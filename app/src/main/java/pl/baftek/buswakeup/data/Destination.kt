package pl.baftek.buswakeup.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import pl.baftek.buswakeup.DEFAULT_POSITION

@Entity(tableName = "destinations")
data class Destination(
        @PrimaryKey var timestamp: Long = 0L,
        var position: LatLng = DEFAULT_POSITION,
        var radius: Double = 0.0
)