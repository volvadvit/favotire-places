package com.volvadvit.memoriesmap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.volvadvit.memoriesmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var objectSerializer: ObjectSerializer

    companion object {
        @JvmStatic
        internal fun checkPermission(context: Context) =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        internal var listAddress: MutableList<String> = mutableListOf()
        internal var listLocation: MutableList<String> = mutableListOf()
        internal val timeStampMap = mutableMapOf<String, String>()
        internal lateinit var mAdapter: LocationAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()
        getUserData()
        initRecyclerAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        /** Save to List<>(listAddress, listLocation) */
        objectSerializer.saveData()
    }

    fun showMap(view: View) {
        // on click method
        if (checkPermission(this)) {
            val intent = Intent(this, MapsActivity::class.java)
                Toast.makeText(this, "Long press on place to save it", Toast.LENGTH_LONG).show()
                intent.putExtra("Location", "emptyExtra")
            startActivity(intent)
        } else {
            requestPermissions()
        }
    }

    private fun initRecyclerAdapter() {
        mAdapter = LocationAdapter(listAddress) {
            // Recycler Listener
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

    private fun getUserData() {
        objectSerializer = ObjectSerializer(this)
        objectSerializer.fillListFromPreferences()
    }

    private fun requestPermissions() {
        if (!checkPermission(this)) {
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

        if (!checkPermission(this)) {
            Toast.makeText(this, "You should give permission", Toast.LENGTH_SHORT).show()
            requestPermissions()
        }
    }
}