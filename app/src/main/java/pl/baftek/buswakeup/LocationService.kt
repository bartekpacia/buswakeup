package pl.baftek.buswakeup

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer

class LocationService : Service() {
    private val TAG = "LocationService"
    private val ACTION_STOP_SERVICE = "stop_service"

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
        Log.d(TAG, "Service destroyed")

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}