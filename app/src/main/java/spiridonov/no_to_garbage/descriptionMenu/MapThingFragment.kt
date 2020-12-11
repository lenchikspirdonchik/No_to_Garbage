package spiridonov.no_to_garbage.descriptionMenu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import spiridonov.no_to_garbage.R


class MapThingFragment : Fragment() {
    private lateinit var msp: SharedPreferences
    private val KEY_THING = "thing"
    private lateinit var txtMap: TextView
    private lateinit var mapview: MapView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_mapview, container, false)
        mapview = root.findViewById(R.id.mapview) as MapView
//        txtMap = root.findViewById(R.id.txtMapFragment)

        msp = this.requireActivity().getSharedPreferences("things", Context.MODE_PRIVATE)
        if (msp.contains(KEY_THING)) {
            val mainCategory = msp.getString(KEY_THING, "").toString()

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
                                mapview.map.mapObjects.addPlacemark(
                                    Point(value[0].toDouble(), value[1].toDouble()),
                                    ImageProvider.fromResource(context, R.drawable.marker55)
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