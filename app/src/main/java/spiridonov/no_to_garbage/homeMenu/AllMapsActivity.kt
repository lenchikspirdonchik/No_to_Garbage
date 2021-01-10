package spiridonov.no_to_garbage.homeMenu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_all_maps.*
import spiridonov.no_to_garbage.R

class AllMapsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("fd59b9d8-89f7-4bc6-aac0-48391066dd80")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_all_maps)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
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

                        val geopointReference = garbageReference.child("map$mapNumber")

                        /*val hintReference = garbageReference.child("mapHint$mapNumber")
                        var hint = ""
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
                                mapview.map.mapObjects.addPlacemark(
                                    Point(value[0].toDouble(), value[1].toDouble()),
                                    ImageProvider.fromResource(
                                        this@AllMapsActivity,
                                        R.drawable.marker55
                                    )
                                )
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
            })
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

