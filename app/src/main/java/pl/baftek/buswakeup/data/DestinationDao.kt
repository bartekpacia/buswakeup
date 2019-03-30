package pl.baftek.buswakeup.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DestinationDao {

    @Query("SELECT * FROM destinations WHERE timestamp = (SELECT MAX(timestamp) FROM destinations)")
    fun getDestination(): Destination?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDestination(destination: Destination)
}