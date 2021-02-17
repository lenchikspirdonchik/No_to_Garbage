package spiridonov.no_to_garbage.descriptionMenu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.homeMenu.UserMapData
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement


class MapThingFragment : Fragment() {
    private lateinit var msp: SharedPreferences
    private val KEY_THING = "thing"
    private lateinit var txtMap: TextView
    private lateinit var mapview: MapView
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_mapview, container, false)
        mapview = root.findViewById(R.id.mapview) as MapView
//        txtMap = root.findViewById(R.id.txtMapFragment)

        mapview.map.mapObjects.addTapListener { varin, point ->
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

        val handler = Handler()
        val thread = Thread {
            msp = this.requireActivity().getSharedPreferences("things", Context.MODE_PRIVATE)
            if (msp.contains(KEY_THING)) {
                val mainCategory = msp.getString(KEY_THING, "").toString()
                this.url = String.format(this.url, this.host, this.port, this.database);
                try {
                    Class.forName("org.postgresql.Driver");
                    val connection = DriverManager.getConnection(url, user, pass);
                    val st: Statement = connection.createStatement()
                    Log.d("mainCategory", mainCategory)
                    val rs: ResultSet =
                        st.executeQuery("select * from no2garbage_map where category='$mainCategory'")

                    while (rs.next()) {
                        val dbWhoAdd: String? = rs.getString("who_add")?.toString()
                        val dbLatLang: List<String> = rs.getString("lat_lang").split(",")
                        val dbHint = rs.getString("hint").toString()
                        handler.post {
                            val data = UserMapData(category = mainCategory, hint = dbHint)
                            mapview.map.mapObjects.addPlacemark(
                                Point(dbLatLang[0].toDouble(), dbLatLang[1].toDouble()),
                                ImageProvider.fromResource(context, R.drawable.marker55)
                            ).userData = data

                            mapview.map.move(
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

        }

        thread.start()
        return root
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }
}
/*
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

                                    val data = UserMapData(category = mainCategory, hint = hint)
                                    mapview.map.mapObjects.addPlacemark(
                                        Point(value[0].toDouble(), value[1].toDouble()),
                                        ImageProvider.fromResource(context, R.drawable.marker55)
                                    ).userData = data

                                    mapview.map.move(
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
                })}*/