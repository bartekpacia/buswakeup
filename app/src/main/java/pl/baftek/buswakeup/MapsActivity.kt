package pl.baftek.buswakeup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import pl.baftek.buswakeup.data.AppDatabase
import pl.baftek.buswakeup.data.Destination

private const val TAG = "MapsActivityLog"
private const val RC_PERMISSION_LOCATION = 9001

@SuppressWarnings("MissingPermission")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var currentDestination: Destination? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        handlePermissions()

        currentDestination = AppDatabase.getInstance(this).destinationDao().getDestination()
        val serviceIntent = Intent(this, LocationService::class.java)
        buttonService.setOnClickListener { startService(serviceIntent) }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun handlePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RC_PERMISSION_LOCATION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION_LOCATION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.error_location_permission, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, AppDatabase.getInstance(this).destinationDao().getDestination().toString())

        map = googleMap
        map.isMyLocationEnabled = true
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(
                currentDestination!!.latitude,
                currentDestination!!.longitude),
            12f))

        // Add a marker
        val marker = map.addMarker(MarkerOptions().position(
            LatLng(
                currentDestination!!.latitude,
                currentDestination!!.longitude
            )).title("Destination"))

        map.setOnMapClickListener { latLng ->
            marker.position = latLng

            val newDestination = Destination(System.nanoTime(), latitude = latLng.latitude, longitude = latLng.longitude)
            Log.d(TAG, newDestination.toString())
            AppDatabase.getInstance(this).destinationDao().insertDestination(newDestination)
            Log.d(TAG, currentDestination.toString())
            Toast.makeText(this, "latitude: ${latLng.latitude}, longitude: ${latLng.longitude}", Toast.LENGTH_SHORT).show()
        }
    }


}
