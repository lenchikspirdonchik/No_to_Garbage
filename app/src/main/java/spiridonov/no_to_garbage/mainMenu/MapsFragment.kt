package spiridonov.no_to_garbage.mainMenu

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.model.LatLng
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
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.homeMenu.UserMapData
import java.util.*


class MapsFragment : Fragment(), GeoObjectTapListener, InputListener {
    var myCoordinates: LatLng? = null
    private lateinit var mapV: MapView
    private var number = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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


        mapV.map.addTapListener(this);
        mapV.map.addInputListener(this);
        mapV.map.mapObjects.addTapListener { varin, point ->
            val data: UserMapData = varin.userData as UserMapData

            val pDialog = SweetAlertDialog(context, SweetAlertDialog.BUTTON_POSITIVE)
            pDialog.progressHelper.barColor = Color.parseColor("#264599")
            pDialog.titleText = data.category
            pDialog.contentText = data.hint
            pDialog.confirmText = "Готово"
            pDialog.cancelText = "Открыть в картах"
            pDialog.progressHelper.rimColor = Color.parseColor("#264599")
            pDialog.setCancelable(false)
            pDialog.setConfirmClickListener {
                pDialog.dismiss()
            }
            pDialog.setCancelClickListener {
                Log.d("pDialog", "Открытие карт")
                val mapUri: Uri =
                    Uri.parse("geo:0,0?q=${point.latitude},${point.longitude}(${data.category})")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            pDialog.progressHelper.spin()
            pDialog.show()
            true
        }





        btn.setOnClickListener {
            if (hint.text.toString() == "" || myCoordinates == null) {
                Toast.makeText(requireContext(), getString(R.string.addSnippet), Toast.LENGTH_LONG)
                    .show()
            } else {
                val firebaseDate = FirebaseDatabase.getInstance()
                val rootReference = firebaseDate.reference
                val garbageReference = rootReference.child("GarbageInformation").child(category)
                val map = garbageReference.child("map$number")
                val hintDatabase = garbageReference.child("mapHint$number")
                val latitude = String.format(Locale.ENGLISH, "%.6f", myCoordinates!!.latitude)
                val longitude = String.format(Locale.ENGLISH, "%.6f", myCoordinates!!.longitude)
                map.setValue("$latitude,$longitude")
                hintDatabase.setValue(hint.text.toString())
                val pDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                pDialog.progressHelper.barColor = Color.parseColor("#264599")
                pDialog.titleText = "Вы успешно добавили место"
                pDialog.contentText = "Спасибо, что делаете приложение лучше!"
                pDialog.confirmText = "Готово"
                pDialog.progressHelper.rimColor = Color.parseColor("#264599")
                pDialog.setCancelable(false)
                pDialog.setConfirmClickListener {
                    btn.setBackgroundColor(Color.RED)
                    btn.isEnabled = false
                    pDialog.dismiss()
                }
                pDialog.progressHelper.spin()
                pDialog.show()

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

                            val data = UserMapData(category = category, hint = hint)
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
            //mapV.map.mapObjects.addPlacemark(geoObjectTapEvent.geoObject.)
            Log.d("map", selectionMetadata.toString())
        }
        return selectionMetadata != null
    }

    override fun onMapTap(p0: Map, point: Point) {
        Log.d("map", "onMapTap")
        mapV.map.mapObjects.addPlacemark(
            Point(point.latitude, point.longitude),
            ImageProvider.fromResource(context, R.drawable.marker55)
        )
        myCoordinates = LatLng(point.latitude, point.longitude)
        mapV.map.deselectGeoObject()
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        Log.d("map", "onMapLongTap")
    }


}