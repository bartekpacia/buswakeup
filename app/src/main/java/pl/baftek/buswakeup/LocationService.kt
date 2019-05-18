package pl.baftek.buswakeup

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import pl.baftek.buswakeup.activities.MapsActivity
import pl.baftek.buswakeup.data.AppDatabase
import pl.baftek.buswakeup.dsl.db
import pl.baftek.buswakeup.dsl.distanceFrom

private const val TAG = "LocationService"
private const val ACTION_STOP_SERVICE = "stop_service"

class LocationService : Service() {
    private val notificationId = 1

    private val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    private lateinit var notificationManager: NotificationManager
    private lateinit var vibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer

    private val observer = Observer<Location> { userLocation ->
        if(userLocation == null) return@Observer
        
        val destination = db().destinationDao().getDestinationSync()
        val distance = userLocation.distanceFrom(destination.position)
        val distanceKm = distance.toFloat() / 1000

        val toastText = if (distance < destination.radius) {
            if (!mediaPlayer.isPlaying) mediaPlayer.start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(500, 500)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, 0))
            } else {
                val pattern = longArrayOf(500, 500)
                vibrator.vibrate(pattern, 0)
            }

            "You are less than ${destination.radius.toInt()} m from the destination!"
        } else "${getString(R.string.distance_from_destination)} $distanceKm km"

        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()

        val text = "${getString(R.string.distance_from_destination)} $distanceKm km"
        val notification = buildNotification(getString(R.string.location_tracking), text)
        notificationManager.notify(notificationId, notification)
    }

    override fun onCreate() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
        mediaPlayer.setDataSource(this, ringtoneUri)
        mediaPlayer.prepare()

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) stopSelf()

        CurrentLocationListener.getInstance(applicationContext).observeForever(observer)

        startForeground(notificationId, buildNotification(title = getString(R.string.location_tracking), text = getString(R.string.waiting_for_location)))

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
        CurrentLocationListener.getInstance(applicationContext).removeObserver(observer) // Maybe observers aren't 'deactivated'?
        mediaPlayer.stop()
        vibrator.cancel()
        Log.d(TAG, "Service destroyed")

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}