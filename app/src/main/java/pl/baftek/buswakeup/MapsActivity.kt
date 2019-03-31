package pl.baftek.buswakeup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import pl.baftek.buswakeup.data.AppDatabase
import pl.baftek.buswakeup.data.Destination

@SuppressWarnings("MissingPermission")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "MapsActivity"

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val serviceIntent = Intent(this, LocationService::class.java)

        buttonService.setOnClickListener { startService(serviceIntent) }

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
            marker.position = latLng

            val destination = Destination(System.nanoTime(), latitude = latLng.latitude, longitude = latLng.longitude)
            Log.d(TAG, destination.toString())
            AppDatabase.getInstance(this).destinationDao().insertDestination(destination)
            Log.d(TAG, AppDatabase.getInstance(this).destinationDao().getDestination().toString())
            Toast.makeText(this, "latitude: ${latLng.latitude}, longitude: ${latLng.longitude}", Toast.LENGTH_SHORT).show()
        }
    }


}
