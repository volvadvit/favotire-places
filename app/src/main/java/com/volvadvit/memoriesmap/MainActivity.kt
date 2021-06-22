package com.volvadvit.memoriesmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.volvadvit.memoriesmap.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    companion object {
        val listAddress: MutableList<String> = mutableListOf()
        val listLocation: MutableList<String> = mutableListOf()
        val timeStampMap = mutableMapOf<String, String>()

        lateinit var mAdapter: LocationAdapter
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        mAdapter = LocationAdapter(listAddress) {
            val intent = Intent(this, MapsActivity::class.java)
            if (listLocation.isNotEmpty()) {
                intent.putExtra("Location", listLocation[it])
            } else {
				intent.putExtra("Location", "emptyExtra")
                Toast.makeText(this, "Location is empty", Toast.LENGTH_SHORT).show()
            }
            startActivity(intent)
        }

        binding.recyclerView.adapter = mAdapter
    }

    fun showMap(view: View) {
        if (checkPermission()) {
            val intent = Intent(this, MapsActivity::class.java)
                Toast.makeText(this, "Long press to add place", Toast.LENGTH_SHORT).show()
                intent.putExtra("Location", "emptyExtra")
            startActivity(intent)
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (!checkPermission()) {
            Toast.makeText(this, "You should give permission", Toast.LENGTH_SHORT).show()
            requestPermissions()
        }
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}