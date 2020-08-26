package spiridonov.no_to_garbage.mainMenu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import spiridonov.no_to_garbage.R
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback {
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var map: GoogleMap
    private lateinit var coordinates: LatLng
    private var number = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val root: View = inflater.inflate(R.layout.fragment_maps, container, false)
        val spinner = root.findViewById<Spinner>(R.id.spinnerMap)
        val btn = root.findViewById<Button>(R.id.btnAddMaps)
        var category = allGarbage[0]
        val hint = root.findViewById<EditText>(R.id.editTextMap)
        val adaptermain: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                allGarbage
            )
        adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adaptermain
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                hint.setText("")
                showExistMap(allGarbage[selectedItemPosition], colors[selectedItemPosition])
                category = allGarbage[selectedItemPosition]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btn.setOnClickListener {
            if (hint.text.toString() == "") {
                Toast.makeText(requireContext(), getString(R.string.addSnippet), Toast.LENGTH_LONG)
                    .show()
            } else {
                val firebaseDate = FirebaseDatabase.getInstance()
                val rootReference = firebaseDate.reference
                val garbageReference = rootReference.child("GarbageInformation").child(category)
                val map = garbageReference.child("map$number")
                val hintDatabase = garbageReference.child("mapHint$number")
                val latitude = String.format(Locale.ENGLISH, "%.6f", coordinates.latitude)
                val longitude = String.format(Locale.ENGLISH, "%.6f", coordinates.longitude)
                map.setValue("$latitude,$longitude")
                hintDatabase.setValue(hint.text.toString())
                Toast.makeText(requireContext(), getString(R.string.done), Toast.LENGTH_LONG).show()

            }
        }
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    private fun showExistMap(category: String, color: Float) {
        map.clear()
        val getData = Thread(Runnable {
            val firebaseDate = FirebaseDatabase.getInstance()
            val rootReference = firebaseDate.reference
            val garbageReference =
                rootReference.child("GarbageInformation").child(category)
            garbageReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    var trueMapNumber = (snapshot.childrenCount - 2) / 2
                    if (trueMapNumber < 0) trueMapNumber = 0
                    number = trueMapNumber
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
                                    map,
                                    LatLng(value[0].toDouble(), value[1].toDouble()),
                                    hint,
                                    category,
                                    color
                                )

                            }
                        })

                        mapNumber++
                    }
                }
            })

        })

        getData.start()
    }


    private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(MarkerOptions().position(latLng))
            coordinates = latLng
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
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
                CameraPosition.Builder().target(point).zoom(12f).build()
            )
        )
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
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


}