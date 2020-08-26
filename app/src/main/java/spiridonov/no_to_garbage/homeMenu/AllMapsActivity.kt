package spiridonov.no_to_garbage.homeMenu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import spiridonov.no_to_garbage.R

class AllMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var map: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_maps)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val allGarbage = arrayOf(
            getString(R.string.BTN_Jars),
            getString(R.string.BTN_Bottles),
            getString(R.string.BTN_Сontainers),
            getString(R.string.BTN_Box),
            getString(R.string.BTN_GoodClothes),
            getString(R.string.BTN_BadClothes),
            getString(R.string.BTN_Battery),
            getString(R.string.BTN_Paper),
            getString(R.string.BTN_Technic)
        )
        val colors = arrayOf(
            335.6F,
            202.5F,
            30F,
            50.3F,
            158.4F,
            183.4F,
            104.7F,
            72.1F,
            50.4F
        )
        map = googleMap
        val getData = Thread(Runnable {
            for (i in 0..allGarbage.lastIndex) {
                val firebaseDate = FirebaseDatabase.getInstance()
                val rootReference = firebaseDate.reference
                val garbageReference =
                    rootReference.child("GarbageInformation").child(allGarbage[i])
                garbageReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val trueMapNumber = (snapshot.childrenCount - 2) / 2

                        var mapNumber = 0
                        while (mapNumber < trueMapNumber) {
                            var hint = ""
                            val geopointReference = garbageReference.child("map$mapNumber")
                            val hintReference = garbageReference.child("mapHint$mapNumber")

                            hintReference.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {}

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    hint = snapshot.getValue(String::class.java)!!
                                }
                            })

                            geopointReference.addValueEventListener(object :
                                ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {}
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val value: List<String> =
                                        snapshot.getValue(String::class.java)!!.split(",")
                                    createMarker(
                                        googleMap,
                                        LatLng(value[0].toDouble(), value[1].toDouble()),
                                        hint,
                                        allGarbage[i],
                                        colors[i]
                                    )

                                }
                            })

                            mapNumber++
                        }
                    }
                })
            }
        })

        getData.start()
        enableMyLocation()
        setMapLongClick()


    }

    private fun createMarker(
        googleMap: GoogleMap,
        point: LatLng,
        hint: String,
        title: String,
        color: Float
    ) {

        googleMap.addMarker(
            MarkerOptions().position(point).title(title).snippet(hint).icon(
                BitmapDescriptorFactory.defaultMarker(color)
            )
        )
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(point).zoom(14f).build()
            )
        )
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng).title(latLng.toString())
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

