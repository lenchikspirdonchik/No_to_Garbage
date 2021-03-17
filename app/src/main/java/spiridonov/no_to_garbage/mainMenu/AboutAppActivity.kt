package spiridonov.no_to_garbage.mainMenu


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_app.*
import spiridonov.no_to_garbage.Admin.AdminActivity
import spiridonov.no_to_garbage.R

class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        txtVK.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/leonid.spiri"))
            startActivity(browserIntent)
        }
        txtIn.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/leonid-spiridonov-424a601ab/")
            )
            startActivity(browserIntent)
        }
        txtGmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "spiridonov.production@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "О приложении школьных выборов")
            startActivity(Intent.createChooser(emailIntent, "Отправка сообщения"))
        }

        imgLogo.setOnLongClickListener {
            val mintent = Intent(this, AdminActivity::class.java)
            startActivity(mintent)
            true
        }

    }


}