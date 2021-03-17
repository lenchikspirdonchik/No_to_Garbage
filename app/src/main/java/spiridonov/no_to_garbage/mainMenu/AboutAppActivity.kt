package spiridonov.no_to_garbage.mainMenu


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import spiridonov.no_to_garbage.Admin.AdminActivity
import spiridonov.no_to_garbage.R

class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_about_app)
        val VK = findViewById<TextView>(R.id.txtVK)
        val In = findViewById<TextView>(R.id.txtIn)
        val Gmail = findViewById<TextView>(R.id.txtGmail)
        val img = findViewById<ImageView>(R.id.imgLogo)


        VK.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/leonid.spiri"))
            startActivity(browserIntent)
        }
        In.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/leonid-spiridonov-424a601ab/")
            )
            startActivity(browserIntent)
        }
        Gmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "spiridonov.production@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "О приложении школьных выборов")
            startActivity(Intent.createChooser(emailIntent, "Отправка сообщения"))
        }

        img.setOnLongClickListener {
            val mintent = Intent(this, AdminActivity::class.java)
            startActivity(mintent)
            true
        }

    }


}