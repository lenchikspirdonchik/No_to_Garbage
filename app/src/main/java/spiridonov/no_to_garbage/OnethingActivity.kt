package spiridonov.no_to_garbage


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.activity_onething.*


class OnethingActivity : AppCompatActivity() {
    lateinit var mainCategory:String
    private val TAG = "DocSnippets"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("c7cf7f99-a766-4d4b-bd41-450bbb95057c")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_onething)
        val mintent = intent
        mainCategory = mintent.extras?.getString("KEY_CATEGORY").toString()
        val txt = findViewById<TextView>(R.id.textView)
        var mapview = findViewById<View>(R.id.mapview) as MapView
        mapview.map.move(CameraPosition(Point(60.012845, 30.279540), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F), null
        )
        val db = Firebase.firestore
        db.collection(mainCategory).get().addOnSuccessListener { result ->
            for (document in result) {
                txt.text = "${document.getString("Head")}\n${document.getString("Body")}"
                val geoPoint = document.getGeoPoint("Map")
                mapview.map.mapObjects.addPlacemark(Point(geoPoint!!.latitude, geoPoint.longitude))
                Log.w(TAG, "Geopoint = $geoPoint")

            }
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
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
