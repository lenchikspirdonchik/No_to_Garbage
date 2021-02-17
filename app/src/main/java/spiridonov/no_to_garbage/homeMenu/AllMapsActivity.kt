package spiridonov.no_to_garbage.homeMenu

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_all_maps.*
import spiridonov.no_to_garbage.R
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement


class AllMapsActivity : AppCompatActivity() {
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("fd59b9d8-89f7-4bc6-aac0-48391066dd80")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_all_maps)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)


        mapview.map.mapObjects.addTapListener { varin, point ->
            val data: UserMapData = varin.userData as UserMapData

            val pDialog = SweetAlertDialog(this, SweetAlertDialog.BUTTON_POSITIVE)
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

            this.url = String.format(this.url, this.host, this.port, this.database);
            try {
                Class.forName("org.postgresql.Driver");
                val connection = DriverManager.getConnection(url, user, pass);
                val st: Statement = connection.createStatement()
                val rs: ResultSet =
                    st.executeQuery("select * from no2garbage_map")
                while (rs.next()) {
                    val dbWhoAdd: String? = rs.getString("who_add")?.toString()
                    val dbLatLang: List<String> = rs.getString("lat_lang").split(",")
                    val dbHint = rs.getString("hint").toString()
                    val dbCategory = rs.getString("category").toString()
                    handler.post {
                        val data = UserMapData(category = dbCategory, hint = dbHint)
                        mapview.map.mapObjects.addPlacemark(
                            Point(dbLatLang[0].toDouble(), dbLatLang[1].toDouble()),
                            ImageProvider.fromResource(this, R.drawable.marker55)
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
        thread.start()
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


class UserMapData(val category: String, val hint: String)
/*
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

                           val geopointReference = garbageReference.child("map$mapNumber")

                           val hintReference = garbageReference.child("mapHint$mapNumber")
                           var hint = ""
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
                                   val data = UserMapData(category = allGarbage[i], hint = hint)
                                   mapview.map.mapObjects.addPlacemark(
                                       Point(value[0].toDouble(), value[1].toDouble()),
                                       ImageProvider.fromResource(
                                           this@AllMapsActivity,
                                           R.drawable.marker55
                                       )
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
               })*/
