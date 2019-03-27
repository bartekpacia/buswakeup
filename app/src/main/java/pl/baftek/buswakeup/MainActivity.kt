package pl.baftek.buswakeup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // TODO Add requesting permissions at runtime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serviceIntent = Intent(this, LocationService::class.java)

        buttonMap.setOnClickListener { startActivity(Intent(this, MapsActivity::class.java)) }

        serviceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startService(serviceIntent)
            } else {
                stopService(serviceIntent)
            }
        }
    }
}