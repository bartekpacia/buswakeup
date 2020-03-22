package pl.baftek.buswakeup.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.baftek.buswakeup.data.AppDatabase
import pl.baftek.buswakeup.data.Destination

class MapsActivityViewModel(val app: Application) : AndroidViewModel(app) {
    enum class RadiusStatus { IDLE, INCREASING, DECREASING }

    val radius: MutableLiveData<RadiusStatus> = MutableLiveData()

    fun getDestination(): LiveData<Destination> =
        AppDatabase.getInstance(app.applicationContext).destinationDao().getDestination()

    fun setDestination(destination: Destination) {
        AppDatabase.getInstance(app.applicationContext).destinationDao()
            .insertDestination(destination)
    }

    init {
        radius.value = RadiusStatus.IDLE
    }
}
