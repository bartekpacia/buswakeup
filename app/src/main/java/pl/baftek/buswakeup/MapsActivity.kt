package pl.baftek.buswakeup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import pl.baftek.buswakeup.data.AppDatabase
import pl.baftek.buswakeup.data.Destination

@SuppressWarnings("MissingPermission")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val start = LatLng(50.098915, 18.551711)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        if (AppDatabase.getInstance(this).destinationDao().getDestination() == null) {
            AppDatabase.getInstance(this).destinationDao().insertDestination(Destination(System.nanoTime(), start.latitude, start.longitude))
        }

        val serviceIntent = Intent(this, LocationService::class.java)

        serviceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startService(serviceIntent)
            } else {
                stopService(serviceIntent)
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 12f))

        // Add a marker in Sydney and move the camera
        val marker = map.addMarker(MarkerOptions().position(start).title("Marker in Silesia"))
        map.moveCamera(CameraUpdateFactory.newLatLng(start))

        map.setOnMapClickListener { latLng ->
            AppDatabase.getInstance(this).destinationDao().insertDestination(Destination(System.nanoTime(), lat = latLng.latitude, long = latLng.longitude))
            marker.position = latLng
            Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT).show()
        }
    }


}
