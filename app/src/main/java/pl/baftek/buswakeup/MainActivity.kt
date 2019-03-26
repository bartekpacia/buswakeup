package pl.baftek.buswakeup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonMap.setOnClickListener { startActivity(Intent(this, MapsActivity::class.java)) }
        buttonStartService.setOnClickListener {
            val input = editText.text.toString()

            val intent = Intent(this, LocationService::class.java)
            intent.putExtra("input", input)
            startService(intent)
        }
        buttonStopService.setOnClickListener {
            val intent = Intent(this, LocationService::class.java)
            stopService(intent)
        }
    }
}