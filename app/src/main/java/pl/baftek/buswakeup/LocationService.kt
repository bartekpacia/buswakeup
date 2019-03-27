package pl.baftek.buswakeup

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class LocationService : Service() {
    private val TAG = "LocationService"

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("input")

        val notificationIntent = Intent(this, MainActivity::class.java)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(input)
                .setSmallIcon(R.drawable.ic_my_location_24dp)
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}