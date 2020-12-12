package spiridonov.no_to_garbage


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider


class MapTestFragment : Fragment() {
    private lateinit var mapview: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.fragment_map_test, container, false)

        mapview = root.findViewById(R.id.mapview) as MapView
        /*  mapview.map.move(
              CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
              Animation(Animation.Type.SMOOTH, 0F),
              null
          )*/

        //  mapview.map.mapObjects.addPlacemark(Point(55.751574, 37.573856),ImageProvider.fromResource(context, R.drawable.marker))

        val getData = Thread(Runnable {
            val firebaseDate = FirebaseDatabase.getInstance()
            val rootReference = firebaseDate.reference
            val garbageReference = rootReference.child("GarbageInformation").child("Батарейки")
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
                                    Point(
                                        value[0].toDouble(),
                                        value[1].toDouble()
                                    ), ImageProvider.fromResource(context, R.drawable.marker55)
                                )
                                mapview.map.move(
                                    CameraPosition(
                                        Point(value[0].toDouble(), value[1].toDouble()),
                                        11.0f,
                                        0.0f,
                                        0.0f
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

        })
        getData.start()














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