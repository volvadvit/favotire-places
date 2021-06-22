package com.volvadvit.memoriesmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.volvadvit.memoriesmap.databinding.ActivityMapsBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.UnsupportedTemporalTypeException
import java.util.*
import kotlin.concurrent.thread

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var localManager : LocationManager
    private lateinit var localListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        localManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        localListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (checkPermission()) {
                    val lastKnownLocation: Location = localManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                    val locationLatLng = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f))
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        val dataFromExtra = intent.getStringExtra("Location")!!
            if (dataFromExtra != "emptyExtra") {
                val arrayLatLng: Array<String> = dataFromExtra.split("+").toTypedArray()
                val location: LatLng = LatLng(arrayLatLng[0].toDouble(), arrayLatLng[1].toDouble())
                    mMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(MainActivity.timeStampMap["${location.latitude}+${location.longitude}"])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            } else {
                if (checkPermission()) {
                    localManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, localListener)
                } else {
                    finish()
                }
            }
    }

    private fun checkPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

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
                if (!listAddresses[0].locality.isNullOrEmpty()) {
                    address += listAddresses[0].locality
                } else {
                    if (!listAddresses[0].adminArea.isNullOrEmpty()) {
                        address += listAddresses[0].locality
                    }
                }
        }
        return address
    }

    override fun onMapLongClick(p0: LatLng) {
            mMap.addMarker(
                MarkerOptions()
                    .position(p0)
                    .title(getAreaInfo(p0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            val location = "${p0.latitude}+${p0.longitude}"
        MainActivity.timeStampMap[location] = getTimeStamp()

        fillMainActivity(location, getAreaInfo(p0))
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

