package com.volvadvit.memoriesmap


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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.volvadvit.memoriesmap.databinding.ActivityMapsBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.UnsupportedTemporalTypeException
import java.util.*

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
        val areaInfo = getAreaInfo(p0)
        mMap.addMarker(
            MarkerOptions()
                .position(p0)
                .title(areaInfo)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
        val location = "${p0.latitude}+${p0.longitude}"
        MainActivity.timeStampMap[location] = getTimeStamp()

        fillMainActivity(location, areaInfo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        object : AlertDialog.Builder(this){}
            .setIcon(android.R.drawable.ic_menu_help)
            .setTitle("Help")
            .setMessage("Long press on place to save it.\n" +
                    "You can return on main page, and see all saved places\n" +
                    "Click on marker and after on the \"Browse\" button, to google place")
            .setNeutralButton("Close", null)
            .show()
        return super.onOptionsItemSelected(item)
    }

    private fun extrasHandler() {
        val dataFromExtra = intent.getStringExtra("Location") ?: "emptyExtra"
        if (dataFromExtra != "emptyExtra") {
            val locArray = MainActivity.listLocation[dataFromExtra.toInt()].split("+").toTypedArray()
            val location: LatLng = LatLng(locArray[0].toDouble(), locArray[1].toDouble())
            Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show()

            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(MainActivity.listAddress[dataFromExtra.toInt()])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
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
//                    val lastKnownLocation: Location =
//                        localManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: return
                    val locationLatLng =
                        LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f))
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    private fun getAreaInfo(location: LatLng): String {
        var address = ""
            val geoCoder = Geocoder(applicationContext, Locale.getDefault())
            val listAddresses: MutableList<Address> = geoCoder.getFromLocation(
                location.latitude,
                location.longitude, 1
            )

            if (listAddresses.isNotEmpty()) {

                if (!listAddresses[0].thoroughfare.isNullOrEmpty()) {
                    address = listAddresses[0].thoroughfare + " "
                }
                if (!listAddresses[0].subThoroughfare.isNullOrEmpty()) {
                    address += listAddresses[0].subThoroughfare + " "
                }
                if (!listAddresses[0].subLocality.isNullOrEmpty()) {
                    address += listAddresses[0].subLocality + " "
                }
                if (!listAddresses[0].adminArea.isNullOrEmpty()) {
                    address += listAddresses[0].adminArea + " "
                }
            }
        return address
    }

    private fun fillMainActivity(location: String, area: String) {
        MainActivity.listLocation.add(location)
        MainActivity.listAddress.add(area)

        runOnUiThread{
            MainActivity.mAdapter.notifyDataSetChanged()
        }
    }

    private fun getTimeStamp(): String {
        var timeStamp = ""
        try {
            timeStamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(
                    ZoneId.systemDefault()
                )
            )
        } catch (e: UnsupportedTemporalTypeException) {
            Log.e("UnsupportedTemporalTypeException", "Can't get the timezone ::: ${e.message}")
        }
        return timeStamp
    }
}

