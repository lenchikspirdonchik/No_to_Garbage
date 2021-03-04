package spiridonov.no_to_garbage.mainMenu

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


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        val displaymetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displaymetrics)
        val screenWidth = displaymetrics.widthPixels
        val screenHeight = displaymetrics.heightPixels
        val category = arrayOf("Кухня", "Ванная", "Гардеробная", "Кабинет")
        val layout = root.findViewById<LinearLayout>(R.id.linearMain)
        val TLP = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        val TRP = TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val LP = TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        LP.setMargins(15)
        var id = 1
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

            textCategory.text = "${name}"
            trName.addView(textCategory)
            val horizontalScrollView = HorizontalScrollView(context)
            horizontalScrollView.layoutParams = TRP

            val linearscroll = LinearLayout(context)
            linearscroll.orientation = LinearLayout.HORIZONTAL
            horizontalScrollView.addView(linearscroll)
            trPhoto.addView(horizontalScrollView)
            val photocategory = setCategory(name)
            for (i in photocategory) {
                val resID = resources.getIdentifier(i, "drawable", context?.packageName)

                val imageView = ImageView(context)
                val bMap = BitmapFactory.decodeResource(resources, resID)
                val bMapScaled = Bitmap.createScaledBitmap(
                    bMap,
                    screenWidth / 3 + 30, screenHeight / 5, true
                )
                imageView.setImageBitmap(bMapScaled)
                imageView.layoutParams = LP
                imageView.id = id
                imageView.tag = id
                imageView.setTag(imageView.id)
                imageView.setOnClickListener { it
                    val tag: String = it.tag.toString()
                    Toast.makeText(requireContext(), tag, Toast.LENGTH_SHORT).show()

                }
                linearscroll.addView(imageView)
                id++
            }

            table.addView(trName)
            table.addView(trPhoto)

        }

        return root

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
