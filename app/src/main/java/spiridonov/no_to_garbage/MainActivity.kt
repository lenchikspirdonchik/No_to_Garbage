package spiridonov.no_to_garbage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun btnMain(view: View) {
        val rotate: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        view.startAnimation(rotate)
        val mintent = Intent(this@MainActivity, ThingsActivity::class.java)
        val btn:Button = view as Button
        mintent.putExtra("KEY_CATEGORY", view.text)
        startActivity(mintent)
    }
}