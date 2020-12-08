package spiridonov.no_to_garbage


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView


class MapTestFragment : Fragment() {
    private var mapview: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MapKitFactory.setApiKey("fd59b9d8-89f7-4bc6-aac0-48391066dd80");
        MapKitFactory.initialize(context);
        val root = inflater.inflate(R.layout.fragment_map_test, container, false)

        mapview = root.findViewById(R.id.mapview) as MapView
        mapview!!.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )

        mapview!!.map.mapObjects.addPlacemark(Point(55.751574, 37.573856))
        return root
    }


}