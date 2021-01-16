package spiridonov.no_to_garbage

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider


class DeleteMapFragment : Fragment(), GeoObjectTapListener, InputListener {

    private lateinit var mapV: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_delete_map, container, false)


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
        val spinner = root.findViewById<Spinner>(R.id.spinnerMap)
        var category = allGarbage[0]
        mapV = root.findViewById<MapView>(R.id.mapview)


        mapV.map.addTapListener(this)
        mapV.map.addInputListener(this)

        val adaptermain: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                allGarbage
            )
        adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptermain
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                showExistMap(allGarbage[selectedItemPosition])
                category = allGarbage[selectedItemPosition]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
                var number = 0
                var mapNumber = 0
                while (mapNumber < trueMapNumber) {
                    var hint = ""
                    val geopointReference = garbageReference.child("map$mapNumber")
                    val hintReference = garbageReference.child("mapHint$mapNumber")

                    hintReference.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value = snapshot.getValue(String::class.java)
                            if (value != null) hint = value
                        }
                    })

                    geopointReference.addValueEventListener(object :
                        ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val snap: String? =
                                snapshot.getValue(String::class.java)
                            if (snap != null) {
                                val value = snap.split(",")
                                Log.d("mapNumber", number.toString())
                                val data = DataToDelete(
                                    category = category,
                                    hint = hint,
                                    mapnumber = number
                                )
                                number++
                                mapV.map.mapObjects.addPlacemark(
                                    Point(value[0].toDouble(), value[1].toDouble()),
                                    ImageProvider.fromResource(context, R.drawable.marker55)
                                ).userData = data

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
                        }
                    })

                    mapNumber++
                }
            }
        })

    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        Log.d("map", "onObjectTap")
        val selectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        if (selectionMetadata != null) {
            mapV.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
            Log.d("map", selectionMetadata.toString())
        }
        return selectionMetadata != null
    }

    override fun onMapTap(p0: Map, point: Point) {
        Log.d("map", "onMapTap")
        mapV.map.mapObjects.addTapListener { varin, _ ->
            val data: DataToDelete = varin.userData as DataToDelete
            val pDialog = SweetAlertDialog(context, SweetAlertDialog.BUTTON_POSITIVE)
            pDialog.progressHelper.barColor = Color.parseColor("#264599")
            pDialog.titleText = "Вы желаете удалить данную метку"
            pDialog.contentText = "${data.hint} == ${data.mapnumber}"
            pDialog.confirmText = "Закрыть"
            pDialog.cancelText = "Удалить"
            pDialog.progressHelper.rimColor = Color.parseColor("#264599")
            pDialog.setCancelable(false)
            pDialog.setConfirmClickListener {
                pDialog.dismiss()
            }
            pDialog.setCancelClickListener {

                val firebaseDate = FirebaseDatabase.getInstance()
                val rootReference = firebaseDate.reference
                val garbageReference =
                    rootReference.child("GarbageInformation").child(data.category)
                val map = garbageReference.child("map${data.mapnumber}")
                val hintDatabase = garbageReference.child("mapHint${data.mapnumber}")
                map.removeValue()
                hintDatabase.removeValue()
                pDialog.dismiss()
                Toast.makeText(context, "Успешно удалено!", Toast.LENGTH_LONG).show()

            }
            pDialog.progressHelper.spin()
            pDialog.show()
            true
        }
        mapV.map.deselectGeoObject()
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        Log.d("map", "onMapLongTap")
    }

}

private class DataToDelete(val category: String, val hint: String, val mapnumber: Int)