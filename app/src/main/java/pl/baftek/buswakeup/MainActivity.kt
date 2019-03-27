package pl.baftek.buswakeup

import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonMap.setOnClickListener { startActivity(Intent(this, MapsActivity::class.java)) }
        buttonStartService.setOnClickListener {
            val intent = Intent(this, LocationService::class.java)
            intent.putExtra("input", editText.text.toString())
            startService(intent)
        }
        buttonStopService.setOnClickListener {
            val intent = Intent(this, LocationService::class.java)
            stopService(intent)
        }

        CurrentLocationListener.getInstance(applicationContext).observe(this, Observer<Location> { location: Location? ->
            location?.let {
                textLocation.text = "Lat: ${location.latitude} Long: ${location.longitude}"
            }
        })
    }
}