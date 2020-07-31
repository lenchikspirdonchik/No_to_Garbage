package spiridonov.no_to_garbage

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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


class MapThingFragment : Fragment(), OnMapReadyCallback {
    private lateinit var msp: SharedPreferences
    private val KEY_THING = "thing"
    private lateinit var txtMap: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mapview, container, false)
//        txtMap = root.findViewById(R.id.txtMapFragment)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        msp = this.requireActivity().getSharedPreferences("things", Context.MODE_PRIVATE)
        if (msp.contains(KEY_THING) && p0 != null) {
            val mainCategory = msp.getString(KEY_THING, "").toString()
            val getData = Thread(Runnable {
                val firebaseDate = FirebaseDatabase.getInstance()
                val rootReference = firebaseDate.reference
                val garbageReference = rootReference.child("GarbageInformation").child(mainCategory)
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

                            geopointReference.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {}
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val value: List<String> =
                                        snapshot.getValue(String::class.java)!!.split(",")
                                    createMarker(
                                        p0,
                                        LatLng(value[0].toDouble(), value[1].toDouble()),
                                        hint
                                    )

                                }
                            })

                            mapNumber++
                        }
                    }
                })
            })
            getData.start()
            // setMapLongClick(map = p0)

        }

    }

    private fun createMarker(googleMap: GoogleMap, point: LatLng, hint: String) {
        googleMap.addMarker(MarkerOptions().position(point).title(hint)).setIcon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(point).zoom(12f).build()
            )
        )
    }


    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng).title(latLng.toString())
            )
        }
    }

}