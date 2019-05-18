package pl.baftek.buswakeup.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.toast
import pl.baftek.buswakeup.BuildConfig
import pl.baftek.buswakeup.LocationService
import pl.baftek.buswakeup.R
import pl.baftek.buswakeup.data.Destination
import pl.baftek.buswakeup.dsl.db

private const val TAG = "MapsActivityLog"
private const val RC_PERMISSION_LOCATION = 9001

@SuppressWarnings("MissingPermission")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var destination: Destination

    // Initialized in onMapReady()
    private lateinit var marker: Marker
    private lateinit var circle: Circle

    private var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        db().destinationDao().getDestination().observe(this, Observer<Destination> {
            toast("observed db update")
            Log.d(TAG, "observed db update")

            destination = it
            updateMap()
        })

        val serviceIntent = Intent(this, LocationService::class.java)
        buttonService.setOnClickListener { startService(serviceIntent) }
        buttonLess.setOnClickListener { if (circle.radius >= 50) circle.radius -= 10; updateDestination() }
        buttonMore.setOnClickListener { circle.radius += 10; updateDestination() }
        textVersion.text = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun handlePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RC_PERMISSION_LOCATION)
            }
        } else {
            map.isMyLocationEnabled = true
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        handlePermissions()

        map.setOnMapClickListener { latLng ->
            destination.position = latLng
            updateDestination()

            Log.d(TAG, "Current destination: $destination")

            updateMap()
        }
    }

    private fun updateDestination() {
        val newDestination = Destination(System.nanoTime(), position = destination.position, radius = circle.radius)
        db().destinationDao().insertDestination(newDestination)
    }


    private fun updateMap() {
        map.clear()

        // Add a marker
        marker = map.addMarker(MarkerOptions()
                .position(destination.position)
                .title(getString(R.string.destination)))

        circle = map.addCircle(CircleOptions()
                .center(destination.position)
                .radius(destination.radius)
                .fillColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .strokeColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)))

        if (firstRun) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination.position, 12f))
            firstRun = false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION_LOCATION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.error_location_permission, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                map.isMyLocationEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mapType: Int = when (item.itemId) {
            R.id.map_normal -> MAP_TYPE_NORMAL
            R.id.map_satellite -> MAP_TYPE_SATELLITE
            R.id.map_terrain -> MAP_TYPE_TERRAIN
            R.id.map_hybrid -> MAP_TYPE_HYBRID
            else -> return super.onOptionsItemSelected(item)
        }

        map.mapType = mapType
        item.isChecked = true
        return true
    }
}