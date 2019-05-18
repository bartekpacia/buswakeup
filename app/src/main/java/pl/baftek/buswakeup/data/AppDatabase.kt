package pl.baftek.buswakeup.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.baftek.buswakeup.DATABASE_NAME
import pl.baftek.buswakeup.DEFAULT_POSITION
import pl.baftek.buswakeup.DEFAULT_RADIUS

@Database(entities = [Destination::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun destinationDao(): DestinationDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = buildDatabase(context)
            }
            if ((instance as AppDatabase).destinationDao().getDestination() == null) {
                (instance as AppDatabase).populateInitialData()
            }
            return instance as AppDatabase
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    private fun populateInitialData() {
        if (this.destinationDao().getDestination() == null) {
            val destination = Destination(System.nanoTime(), DEFAULT_POSITION, radius = DEFAULT_RADIUS)

            this.destinationDao().insertDestination(destination)
        }
    }
}