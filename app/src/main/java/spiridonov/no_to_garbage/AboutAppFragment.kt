package spiridonov.no_to_garbage


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AboutAppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_about_app, container, false)
        val VK = root.findViewById<TextView>(R.id.txtVK)
        val In = root.findViewById<TextView>(R.id.txtIn)
        val Gmail = root.findViewById<TextView>(R.id.txtGmail)
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



        return root
    }


}