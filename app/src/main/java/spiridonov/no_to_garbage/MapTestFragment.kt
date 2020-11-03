package spiridonov.no_to_garbage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.dublgis.dgismobile.mapsdk.MapFragment


class MapTestFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_map_test, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as MapFragment
        mapFragment.setup(apiKey = "6b2fec7c-c070-49b3-a037-dfab58bccb95")


        return root
    }


}