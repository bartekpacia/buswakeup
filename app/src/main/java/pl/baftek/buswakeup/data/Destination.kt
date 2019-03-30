package pl.baftek.buswakeup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class Destination(
        @PrimaryKey var timestamp: Long = 0L,
        var lat: Double = 0.0,
        var long: Double = 0.0
)