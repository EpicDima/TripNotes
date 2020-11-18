package com.asistlab.tripnotes.ui

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asistlab.tripnotes.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_choose_point.*
import java.util.*

/**
 * @author EpicDima
 */
class ChoosePointActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val LOCATION_KEY = "location"
        const val ADDRESS_KEY = "address"
    }

    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_point)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        add.setOnClickListener {
            val result = Intent()
            val target = map.cameraPosition.target!!
            val address = getAddress(target)
            if (address.isEmpty()) {
                Toast.makeText(this, getString(R.string.cant_select_place), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            result.putExtra(LOCATION_KEY, target)
            result.putExtra(ADDRESS_KEY, getAddress(target))
            setResult(RESULT_OK, result)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        geocoder = Geocoder(this, Locale.getDefault())
        map.setOnCameraIdleListener {
            target_info.text = getAddress(map.cameraPosition.target!!)
        }
    }

    private fun getAddress(target: LatLng): String {
        val addresses = geocoder.getFromLocation(target.latitude, target.longitude, 1)
        return if (addresses.size > 0) {
            addresses[0].getAddressLine(0).replace("Unnamed Road, ", "")
        } else {
            ""
        }
    }
}