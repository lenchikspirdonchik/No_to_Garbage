package spiridonov.no_to_garbage.mainMenu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import spiridonov.no_to_garbage.R


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)

        val layout = root.findViewById<LinearLayout>(R.id.linearMain)
        val TLP = TableLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )
        val table = TableLayout(context)
        table.layoutParams = TLP

        val ScrollParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        for (j in 0..10) {
            val trName = TableRow(context)
            trName.layoutParams = TLP
            val textCategory = TextView(context)
            textCategory.text = "\ncategory, j = $j"
            textCategory.setTextColor(Color.BLACK)
            textCategory.textSize = 18F
            trName.addView(textCategory)

            val trPhoto = TableRow(context)
            trName.layoutParams = TLP

            val horizontalScrollView = HorizontalScrollView(context)
            horizontalScrollView.layoutParams = TLP

            val linearscroll = LinearLayout(context)
            linearscroll.orientation = LinearLayout.HORIZONTAL
            linearscroll.layoutParams = TLP

            for (i in 0..3) {
                val imageView = ImageView(context)
                val bMap = BitmapFactory.decodeResource(resources, R.drawable.battery)
                val bMapScaled = Bitmap.createScaledBitmap(bMap, 450, 450, true)
                imageView.setImageBitmap(bMapScaled)
                val space = TextView(context)
                space.text = "        "

                linearscroll.addView(imageView)
                linearscroll.addView(space)
                //trPhoto.addView(imageView)
                //trPhoto.addView(space)
            }
            horizontalScrollView.addView(linearscroll)
            trPhoto.addView(horizontalScrollView)
            table.addView(trName)
            table.addView(trPhoto)
        }
        layout.addView(table)


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