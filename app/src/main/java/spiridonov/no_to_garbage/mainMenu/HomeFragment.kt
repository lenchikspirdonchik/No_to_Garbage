package spiridonov.no_to_garbage.mainMenu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        val myinflater = LayoutInflater.from(context)
        val myview = myinflater.inflate(R.layout.category_row, layout, false)

        for (j in 0..2) {
            val table = myview.findViewById<TableLayout>(R.id.categoryTable)

            val trName = table.findViewById<TableRow>(R.id.tableRawName)
            val textCategory = trName.findViewById<TextView>(R.id.txtCategory)
            textCategory.text = "category, j = $j"

            val trPhoto = table.findViewById<TableRow>(R.id.tableRawPhoto)
            for (i in 0..3) {

                val layoutPhoto = trPhoto.findViewById<LinearLayout>(R.id.layoutCategory)
                val imageView = ImageView(context)
                val bMap = BitmapFactory.decodeResource(resources, R.drawable.battery)
                val bMapScaled = Bitmap.createScaledBitmap(bMap, 500, 500, true)
                imageView.setImageBitmap(bMapScaled)
                val space = TextView(context)
                space.text = "        "

                layoutPhoto.addView(imageView)
                layoutPhoto.addView(space)
            }
            table.removeAllViews()
            table.addView(trName)
            table.addView(trPhoto)

        }
        layout.addView(myview)


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