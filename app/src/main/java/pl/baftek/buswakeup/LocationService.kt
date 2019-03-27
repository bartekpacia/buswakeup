package pl.baftek.buswakeup

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer

class LocationService : Service() {
    // TODO Add "stop" button to notification

    private val TAG = "LocationService"
    private val notificationId = 1
    private lateinit var notificationManager: NotificationManager

    private val observer = Observer<Location> { location ->
        val notification = buildNotification("Śledzenie lokalizacji", "Lat: ${location.latitude}, Long: ${location.longitude}")
        notificationManager.notify(notificationId, notification)
    }

    override fun onCreate() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CurrentLocationListener.getInstance(applicationContext).observeForever(observer)

        startForeground(notificationId, buildNotification(title = "Service started", text = ""))

        return START_STICKY
    }

    private fun buildNotification(title: String, text: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setSmallIcon(R.drawable.ic_my_location_24dp)
                .setColorized(true)
                .setColor(resources.getColor(R.color.colorPrimary))
                .build()
    }

    override fun onDestroy() {
        CurrentLocationListener.getInstance(applicationContext).removeObserver(observer)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}