package spiridonov.no_to_garbage.mainMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import spiridonov.no_to_garbage.R
import java.util.*

class MapsFragment : Fragment() {
    private var coordinates: LatLng? = null
    private lateinit var mapV: MapView
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
        val root: View = inflater.inflate(R.layout.fragment_maps, container, false)
        val spinner = root.findViewById<Spinner>(R.id.spinnerMap)
        val btn = root.findViewById<Button>(R.id.btnAddMaps)
        var category = allGarbage[0]
        val hint = root.findViewById<EditText>(R.id.editTextMap)
        mapV = root.findViewById<MapView>(R.id.mapview)
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
                showExistMap(allGarbage[selectedItemPosition])
                category = allGarbage[selectedItemPosition]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btn.setOnClickListener {
            if (hint.text.toString() == "" || coordinates == null) {
                Toast.makeText(requireContext(), getString(R.string.addSnippet), Toast.LENGTH_LONG)
                    .show()
            } else {
                val firebaseDate = FirebaseDatabase.getInstance()
                val rootReference = firebaseDate.reference
                val garbageReference = rootReference.child("GarbageInformation").child(category)
                val map = garbageReference.child("map$number")
                val hintDatabase = garbageReference.child("mapHint$number")
                val latitude = String.format(Locale.ENGLISH, "%.6f", coordinates!!.latitude)
                val longitude = String.format(Locale.ENGLISH, "%.6f", coordinates!!.longitude)
                map.setValue("$latitude,$longitude")
                hintDatabase.setValue(hint.text.toString())
                Toast.makeText(requireContext(), getString(R.string.done), Toast.LENGTH_LONG).show()

            }
        }
        return root
    }


    private fun showExistMap(category: String) {
        mapV.map.mapObjects.clear()
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
                    /* val hintReference = garbageReference.child("mapHint$mapNumber")

                         hintReference.addValueEventListener(object : ValueEventListener {
                             override fun onCancelled(error: DatabaseError) {}

                             override fun onDataChange(snapshot: DataSnapshot) {
                                 hint = snapshot.getValue(String::class.java)!!
                             }
                         })*/

                    geopointReference.addValueEventListener(object :
                        ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value: List<String> =
                                snapshot.getValue(String::class.java)!!.split(",")
                            mapV.map.mapObjects.addPlacemark(
                                Point(value[0].toDouble(), value[1].toDouble()),
                                ImageProvider.fromResource(context, R.drawable.marker55)
                            )
                            mapV.map.move(
                                com.yandex.mapkit.map.CameraPosition(
                                    Point(
                                        value[0].toDouble(),
                                        value[1].toDouble()
                                    ), 14.0f, 0.0f, 0.0f
                                ),
                                Animation(Animation.Type.SMOOTH, 0F),
                                null
                            )

                        }
                    })

                    mapNumber++
                }
            }
        })

    }

}