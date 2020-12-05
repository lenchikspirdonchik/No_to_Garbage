package spiridonov.no_to_garbage

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val alertDialog = AlertDialog.Builder(applicationContext)
        alertDialog.setTitle("PASSWORD");
        alertDialog.setMessage("Enter Password");

        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        alertDialog.setView(input)


        alertDialog.setPositiveButton("Yes Option",
            DialogInterface.OnClickListener { dialog, whichButton -> //What ever you want to do with the value
                val YouEditTextValue: String = input.text.toString()
                Log.d("admin", YouEditTextValue)
            })

        alertDialog.setNegativeButton("No Option",
            DialogInterface.OnClickListener { dialog, whichButton ->
                // what ever you want to do with No option.
            })

        alertDialog.show()




    }
}