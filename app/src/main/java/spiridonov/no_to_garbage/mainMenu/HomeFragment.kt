package spiridonov.no_to_garbage.mainMenu

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.descriptionMenu.OnethingActivity
import spiridonov.no_to_garbage.homeMenu.AddGarbageActivity


class HomeFragment : Fragment() {
    private var screenWidth = 0
    private var screenHeight = 0
    val TLP = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    val TRP = TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    val LP = TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    val imageP = TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    val linearimageP = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        val displaymetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displaymetrics)
        screenWidth = displaymetrics.widthPixels
        screenHeight = displaymetrics.heightPixels
        val category = arrayOf("Кухня", "Ванная", "Гардеробная", "Кабинет")
        val layout = root.findViewById<LinearLayout>(R.id.linearMain)
        imageP.setMargins(15)
        linearimageP.setMargins(15)
        val table = TableLayout(context)
        table.layoutParams = TLP
        layout.addView(table)


        for (name in category) {
            val trName = TableRow(context)
            val trPhoto = TableRow(context)
            trPhoto.layoutParams = LP
            val textCategory = TextView(context)
            textCategory.setTextColor(Color.BLACK)
            textCategory.textSize = 22F
            table.layoutParams = TLP

            textCategory.text = " ${name}"
            trName.addView(textCategory)
            val horizontalScrollView = HorizontalScrollView(context)
            horizontalScrollView.layoutParams = TRP

            val linearscroll = LinearLayout(context)
            linearscroll.orientation = LinearLayout.HORIZONTAL
            horizontalScrollView.addView(linearscroll)
            trPhoto.addView(horizontalScrollView)
            val photocategory = setCategory(name)
            val namecategory = setRusCategory(name)
            for (i in 0..photocategory.lastIndex) {
                val resID =
                    resources.getIdentifier(photocategory[i], "drawable", context?.packageName)
                val linearImage = LinearLayout(context)
                linearImage.orientation = LinearLayout.VERTICAL
                linearImage.layoutParams = linearimageP
                val imageView = ImageView(context)
                val bMap = BitmapFactory.decodeResource(resources, resID)
                val bMapScaled = Bitmap.createScaledBitmap(
                    bMap,
                    screenWidth / 3 + 20,
                    screenHeight / 5 + 15,
                    true
                )
                imageView.setImageBitmap(bMapScaled)
                imageView.layoutParams = TLP
                val text = TextView(context)
                text.layoutParams = TLP
                text.text = namecategory[i]
                imageView.setOnClickListener {
                    it
                    val mintent = Intent(context, OnethingActivity::class.java)
                    val msp = activity?.getSharedPreferences("things", Context.MODE_PRIVATE)
                    val editor = msp?.edit()
                    if (editor != null) {
                        editor.putString("thing", namecategory[i])
                        editor.apply()
                        startActivity(mintent)
                    }

                }
                linearImage.addView(imageView)
                linearImage.addView(text)
                linearscroll.addView(linearImage)
            }

            table.addView(trName)
            table.addView(trPhoto)

        }

        val trName = TableRow(context)
        val trPhoto = TableRow(context)
        trPhoto.layoutParams = LP
        val textCategory = TextView(context)
        textCategory.setTextColor(Color.BLACK)
        textCategory.textSize = 22F
        table.layoutParams = TLP
        textCategory.text = "Другое"
        trName.addView(textCategory)
        val horizontalScrollView = HorizontalScrollView(context)
        horizontalScrollView.layoutParams = TRP
        val linearscroll = LinearLayout(context)
        linearscroll.orientation = LinearLayout.HORIZONTAL
        horizontalScrollView.addView(linearscroll)
        trPhoto.addView(horizontalScrollView)
        val resID = resources.getIdentifier("add_garbage", "drawable", context?.packageName)
        val linearImage = LinearLayout(context)
        linearImage.orientation = LinearLayout.VERTICAL
        linearImage.layoutParams = linearimageP
        val imageView = ImageView(context)
        val bMap = BitmapFactory.decodeResource(resources, resID)
        val bMapScaled =
            Bitmap.createScaledBitmap(bMap, screenWidth / 3 + 20, screenHeight / 5 + 15, true)
        imageView.setImageBitmap(bMapScaled)
        imageView.layoutParams = TLP
        imageView.setOnClickListener {
            val mintent = Intent(context, AddGarbageActivity::class.java)
            startActivity(mintent)
        }
        val text = TextView(context)
        text.layoutParams = TLP
        text.text = "Выкинуть мусор"
        linearImage.addView(imageView)
        linearImage.addView(text)
        linearscroll.addView(linearImage)
        table.addView(trName)
        table.addView(trPhoto)

        return root

    }


    private fun setRusCategory(mainCategory: String): Array<String> {
        return when (mainCategory) {
            "Кухня" -> arrayOf("Стеклянные банки", "Бутылки", "Контейнеры", "Коробки")
            "Ванная" -> arrayOf("Бутылки")
            "Гардеробная" -> arrayOf("Одежда в плохом состоянии", "Одежда в хорошем состоянии")
            "Кабинет" -> arrayOf("Батарейки", "Бумага", "Техника")
            else -> arrayOf("Батарейки", "Бумага", "Техника")
        }
    }

    private fun setCategory(mainCategory: String): Array<String> {
        return when (mainCategory) {
            "Кухня" -> arrayOf("jars", "kitchenbottles", "containers", "box")
            "Ванная" -> arrayOf("bathbottles")
            "Гардеробная" -> arrayOf("badclothes", "goodclothes")
            "Кабинет" -> arrayOf("battery", "paper", "technic")
            else -> arrayOf("battery", "paper", "technic")
        }
    }
}
