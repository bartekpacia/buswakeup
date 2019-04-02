package pl.baftek.buswakeup

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import pl.baftek.buswakeup.data.AppDatabase
import pl.baftek.buswakeup.data.Destination

private const val TAG = "LocationService"
private const val ACTION_STOP_SERVICE = "stop_service"
// TODO Let user change the threshold
private const val THRESHOLD = 100

class LocationService : Service() {
    private val notificationId = 1
    private val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    private lateinit var notificationManager: NotificationManager
    private lateinit var ringtone: Ringtone

    private val observer = Observer<Location> { userLocation ->
        val array = FloatArray(1)

        /** Won't be null (look at [AppDatabase.populateInitialData]**/
        val destination = AppDatabase.getInstance(this).destinationDao().getDestination() as Destination
        Log.d(TAG, destination.toString())

        userLocation?.let {
            Location.distanceBetween(
                it.latitude,
                it.longitude,
                destination.latitude,
                destination.longitude,
                array
            )
        }
        val distance = "%.2f".format(array[0] / 1000)

        val toastText = if (array[0] < THRESHOLD) {
            if (!ringtone.isPlaying) ringtone.play()

            "You are less than $THRESHOLD m from the destination!"
        } else "${getString(R.string.distance_from_destination)} $distance: km"

        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()

        val text = "${getString(R.string.distance_from_destination)} $distance: km"
        val notification = buildNotification(getString(R.string.location_tracking), text)
        notificationManager.notify(notificationId, notification)
    }

    override fun onCreate() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) stopSelf()

        CurrentLocationListener.getInstance(applicationContext).observeForever(observer)

        startForeground(notificationId, buildNotification(title = "Service started", text = "Waiting for location..."))

        return START_STICKY
    }

    private fun buildNotification(title: String, text: String): Notification {
        val notificationIntent = Intent(this, MapsActivity::class.java)

        val cancelIntent = Intent(this, LocationService::class.java)
        cancelIntent.action = ACTION_STOP_SERVICE

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .addAction(R.drawable.ic_my_location_24dp, getString(R.string.turn_off), PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setSmallIcon(R.drawable.ic_my_location_24dp)
                .setColorized(true)
                .setColor(resources.getColor(R.color.colorPrimary))
                .build()
    }

    override fun onDestroy() {
        CurrentLocationListener.getInstance(applicationContext).removeObserver(observer)
        ringtone.stop()
        Log.d(TAG, "Service destroyed")

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}