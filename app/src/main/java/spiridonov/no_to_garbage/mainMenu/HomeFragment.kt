package spiridonov.no_to_garbage.mainMenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.ThingsActivity
import spiridonov.no_to_garbage.homeMenu.AddGarbageActivity
import spiridonov.no_to_garbage.homeMenu.AllMapsActivity
import spiridonov.no_to_garbage.homeMenu.StatisticsActivity


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        val kitchen = root.findViewById<Button>(R.id.btn_kitchen)
        val bath = root.findViewById<Button>(R.id.btn_bathroom)
        val cabinet = root.findViewById<Button>(R.id.btn_cabinet)
        val wardrope = root.findViewById<Button>(R.id.btn_wardrope)
        val btnMain = View.OnClickListener { v ->
            val rotate: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate)
            v.startAnimation(rotate)
            val mintent = Intent(context, ThingsActivity::class.java)
            val btn: Button = v as Button
            mintent.putExtra("KEY_CATEGORY", btn.text)
            startActivity(mintent)

        }
        val naviga_hom = root.findViewById<BottomNavigationView>(R.id.naviga_home)
        naviga_hom.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_statistics -> {
                    val mintent = Intent(context, StatisticsActivity::class.java)
                    startActivity(mintent)
                }
                R.id.navigation_addGarbage -> {
                    val mintent = Intent(context, AddGarbageActivity::class.java)
                    startActivity(mintent)
                }
                R.id.navigation_MapHome -> {
                    val mintent = Intent(context, AllMapsActivity::class.java)
                    startActivity(mintent)
                }
            }

            false
        }


        kitchen.setOnClickListener(btnMain)
        bath.setOnClickListener(btnMain)
        cabinet.setOnClickListener(btnMain)
        wardrope.setOnClickListener(btnMain)


        return root

    }
}