package spiridonov.no_to_garbage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class ThingsActivity : AppCompatActivity() {
    private lateinit var btn_1: Button
    private lateinit var btn_2: Button
    private lateinit var btn_3: Button
    private lateinit var btn_4: Button
    private lateinit var msp: SharedPreferences
    private val KEY_THING = "thing"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_things)
        btn_1 = findViewById(R.id.btn_1)
        btn_2 = findViewById(R.id.btn_2)
        btn_3 = findViewById(R.id.btn_3)
        btn_4 = findViewById(R.id.btn_4)

        val mintent = intent
        val mainCategory = mintent.extras!!.getString("KEY_CATEGORY")
        when (mainCategory) {
            resources.getString(R.string.BTN_Kitchen) -> {
                setText(btn_1, resources.getString(R.string.BTN_Jars), "jars")
                setText(btn_2, resources.getString(R.string.BTN_Bottles), "kitchenbottles")
                setText(btn_3, resources.getString(R.string.BTN_Сontainers), "containers")
                setText(btn_4, resources.getString(R.string.BTN_Box), "box")
            }
            resources.getString(R.string.BTN_Bathroom) -> {
                setText(btn_1, resources.getString(R.string.BTN_Bottles), "bathbottles")
            }
            resources.getString(R.string.BTN_Wardrobe) -> {
                setText(btn_1, resources.getString(R.string.BTN_GoodClothes), "goodclothes")
                setText(btn_2, resources.getString(R.string.BTN_BadClothes), "badclothes")
            }
            resources.getString(R.string.BTN_Сabinet) -> {
                setText(btn_1, resources.getString(R.string.BTN_Battery), "battery")
                setText(btn_2, resources.getString(R.string.BTN_Paper), "paper")
                setText(btn_3, resources.getString(R.string.BTN_Technic), "technic")
            }
        }
    }

    fun btnThing(view: View) {
        val rotate: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        view.startAnimation(rotate)
        val mintent = Intent(this, OnethingActivity::class.java)
        val btn: Button = view as Button
        msp = this.getSharedPreferences("things", Context.MODE_PRIVATE)
        val editor = msp.edit()
        editor.putString(KEY_THING, btn.text.toString())
        editor.apply()
        startActivity(mintent)
    }


    private fun setText(
        button: Button = btn_1,
        txt: String = resources.getString(R.string.BTN_Jars),
        pic: String = "jars"
    ) {
        button.visibility = View.VISIBLE
        button.text = txt
        val resID = resources.getIdentifier(pic, "drawable", packageName)
        button.setBackgroundResource(resID)
    }

}