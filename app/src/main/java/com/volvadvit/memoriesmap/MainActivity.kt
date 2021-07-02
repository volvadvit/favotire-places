package com.volvadvit.memoriesmap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
        internal var timeStampMap = mutableMapOf<String, String>()  // Location to Timestamp
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
        mAdapter = LocationAdapter(listAddress)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_help -> {
                object : AlertDialog.Builder(this) {}
                    .setIcon(android.R.drawable.ic_menu_help)
                    .setTitle("Help")
                    .setMessage("Click \"Add location\" to open the map\n" +
                            "Click on your already saved place, to see it\n" +
                            "Long press to delete place from list")
                    .setNeutralButton("Close", null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}