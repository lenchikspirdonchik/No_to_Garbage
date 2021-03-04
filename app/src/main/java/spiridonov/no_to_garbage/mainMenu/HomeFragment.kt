package spiridonov.no_to_garbage.mainMenu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.core.view.marginRight
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import okhttp3.TlsVersion
import spiridonov.no_to_garbage.R


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)

        val layout = root.findViewById<LinearLayout>(R.id.linearMain)
//TableRow.LayoutParams
        val TLP = TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        TLP.setMargins(15)
        val table = TableLayout(context)
        table.layoutParams = TLP
        val handle = Handler()
        layout.addView(table)
        for (j in 0..10) {
            val thread = Thread {
                val trName = TableRow(context)
                val trPhoto = TableRow(context)
                val textCategory = TextView(context)
                textCategory.setTextColor(Color.BLACK)
                textCategory.textSize = 18F
                table.layoutParams = TLP

                textCategory.text = "\ncategory, j = $j"
                trName.addView(textCategory)
                val horizontalScrollView = HorizontalScrollView(context)
                val linearscroll = LinearLayout(context)
                linearscroll.orientation = LinearLayout.HORIZONTAL
                horizontalScrollView.addView(linearscroll)
                trPhoto.addView(horizontalScrollView)
                for (i in 0..10) {
                    val imageView = ImageView(context)
                    val bMap = BitmapFactory.decodeResource(resources, R.drawable.battery)
                    val bMapScaled = Bitmap.createScaledBitmap(bMap, 200, 200, true)
                    imageView.setImageBitmap(bMapScaled)
                    imageView.layoutParams = TLP
                    linearscroll.addView(imageView)
                }


                handle.post {
                    table.addView(trName)
                    table.addView(trPhoto)
                }
            }
            thread.start()
        }


        /*
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
        wardrope.setOnClickListener(btnMain)*/


        return root

    }

}
