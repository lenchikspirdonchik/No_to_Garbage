package spiridonov.no_to_garbage.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import spiridonov.no_to_garbage.AddGarbageActivity
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.ThingsActivity


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
        val fabb = root.findViewById<FloatingActionButton>(R.id.fab)
        val btnMain = View.OnClickListener { v ->
            val rotate: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate)
            v.startAnimation(rotate)
            val mintent = Intent(context, ThingsActivity::class.java)
            val btn: Button = v as Button
            mintent.putExtra("KEY_CATEGORY", btn.text)
            startActivity(mintent)
        }

        fabb.setOnClickListener {
            val mintent = Intent(context, AddGarbageActivity::class.java)
            startActivity(mintent)
        }



        kitchen.setOnClickListener(btnMain)
        bath.setOnClickListener(btnMain)
        cabinet.setOnClickListener(btnMain)
        wardrope.setOnClickListener(btnMain)


        return root

    }
}