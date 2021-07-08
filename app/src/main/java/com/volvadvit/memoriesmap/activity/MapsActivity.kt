package com.volvadvit.memoriesmap.activity


import android.content.Intent
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.volvadvit.memoriesmap.common.ObjectSerializer
import com.volvadvit.memoriesmap.R
import com.volvadvit.memoriesmap.common.GeoAsyncTask
import com.volvadvit.memoriesmap.databinding.ActivityMapsBinding
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.UnsupportedTemporalTypeException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var localManager : LocationManager
    private lateinit var localListener: LocationListener
    private var markerInfo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.browseButton.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("web", markerInfo)
            startActivity(intent)
        }
        initLocalMng()
    }

    override fun onStop() {
        super.onStop()
        val objectSerializer = ObjectSerializer(this)
        objectSerializer.saveData()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            markerInfo = marker.title!!
            binding.browseButton.visibility = View.VISIBLE
            true
        }
        extrasHandler()
    }

    override fun onMapLongClick(p0: LatLng) {
        val task = GeoAsyncTask(this)
        val areaInfo = try {
            task.execute(p0).get()
        } catch (e: Exception) {
            Log.d("Exception", e.message.toString())
            null
        }
        if (!areaInfo.isNullOrEmpty()){
            val time = getTimeStamp()
            val location = "${p0.latitude}+${p0.longitude}"
            mMap.addMarker(
                MarkerOptions()
                    .position(p0)
                    .title(areaInfo)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )

            MainActivity.listLocation.add(location)
            MainActivity.listAddress.add(areaInfo)
            MainActivity.listTimeStamp.add(time)
            runOnUiThread {
                MainActivity.timeAdapter.notifyDataSetChanged()
            }
        } else {
            Toast.makeText(this, "Something goes wrong...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_help -> {
                object : AlertDialog.Builder(this) {}
                    .setIcon(android.R.drawable.ic_menu_help)
                    .setTitle("Help")
                    .setMessage(
                        "Long press on place to save it.\n" +
                                "You can return on main page, and see all saved places\n" +
                                "Click on marker and after on the \"Browse\" button, to google place"
                    )
                    .setNeutralButton("Close", null)
                    .show()
            }

            android.R.id.home -> {
                this@MapsActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun extrasHandler() {
        val dataFromExtra = intent.getStringExtra("Location") ?: "emptyExtra"
        if (dataFromExtra != "emptyExtra") {
            val locArray = MainActivity.listLocation[dataFromExtra.toInt()].split("+").toTypedArray()
            val location: LatLng = LatLng(locArray[0].toDouble(), locArray[1].toDouble())

            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(MainActivity.listAddress[dataFromExtra.toInt()])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
        } else {
            if (MainActivity.checkPermission(applicationContext)) {
                localManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    localListener
                )
            } else {
                finish()
            }
        }
    }

    private fun initLocalMng() {
        localManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        localListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (MainActivity.checkPermission(applicationContext)) {
                    val locationLatLng =
                        LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 18f))
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    private fun getTimeStamp(): String {
        var timeStamp = ""
        try {
            timeStamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(
                    ZoneId.systemDefault()
                )
            )
        } catch (e: UnsupportedTemporalTypeException) {
            Log.e("UnsupportedTemporalTypeException", "Can't get the timezone ::: ${e.message}")
        }
        return timeStamp
    }
}

