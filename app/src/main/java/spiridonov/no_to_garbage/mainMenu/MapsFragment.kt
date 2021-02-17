package spiridonov.no_to_garbage.mainMenu

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
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
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*


class MapsFragment : Fragment(), GeoObjectTapListener, InputListener {
    var myCoordinates: LatLng? = null
    private lateinit var mapV: MapView
    private var number = 0L
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"

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
        val mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth.currentUser

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
                if (firebaseUser != null) {
                    val latitude = String.format(Locale.ENGLISH, "%.6f", myCoordinates!!.latitude)
                    val longitude = String.format(Locale.ENGLISH, "%.6f", myCoordinates!!.longitude)
                    val latLang = "$latitude,$longitude"
                    Save2SQL(firebaseUser.uid, latLang, category, hint.text.toString())
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
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Для начала необходимо авторизоваться",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        return root

    }

    private fun Save2SQL(uuid: String, latLang: String, category: String, hint: String) {
        val thread = Thread {
            try {
                Class.forName("org.postgresql.Driver");
                val connection = DriverManager.getConnection(url, user, pass);
                val st: Statement = connection.createStatement()
                st.execute(
                    " insert into no2garbage_map (who_add, category, lat_lang, hint)\n" +
                            "VALUES ('$uuid','$category', '$latLang', '$hint');"
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        thread.start()
    }


    private fun showExistMap(mainCategory: String) {
        mapV.map.mapObjects.clear()
        val handler = Handler()
        val thread = Thread {
            this.url = String.format(this.url, this.host, this.port, this.database);
            try {
                Class.forName("org.postgresql.Driver");
                val connection = DriverManager.getConnection(url, user, pass);
                val st: Statement = connection.createStatement()
                Log.d("mainCategory", mainCategory)
                val rs: ResultSet =
                    st.executeQuery("select * from no2garbage_map where category='$mainCategory'")

                while (rs.next()) {
                    val dbLatLang: List<String> = rs.getString("lat_lang").split(",")
                    val dbHint = rs.getString("hint").toString()
                    handler.post {
                        val data = UserMapData(category = mainCategory, hint = dbHint)
                        mapV.map.mapObjects.addPlacemark(
                            Point(dbLatLang[0].toDouble(), dbLatLang[1].toDouble()),
                            ImageProvider.fromResource(context, R.drawable.marker55)
                        ).userData = data

                        mapV.map.move(
                            com.yandex.mapkit.map.CameraPosition(
                                Point(
                                    dbLatLang[0].toDouble(),
                                    dbLatLang[1].toDouble()
                                ), 14.0f, 0.0f, 0.0f
                            ),
                            Animation(Animation.Type.SMOOTH, 0F),
                            null
                        )
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        thread.start()

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