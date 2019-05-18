package pl.baftek.buswakeup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class Destination(
        @PrimaryKey var timestamp: Long = 0L,
        var latitude: Double = -1.0,
        var longitude: Double = -1.0,
        var radius: Double = 0.0
)