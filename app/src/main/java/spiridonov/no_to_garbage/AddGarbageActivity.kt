package spiridonov.no_to_garbage

import android.R.layout
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_garbage.*

class AddGarbageActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private val firebaseUser = mAuth.currentUser
    private val firebaseDate = FirebaseDatabase.getInstance()
    private val rootReference = firebaseDate.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_garbage)
        var allGarbage = arrayOf(
            resources.getString(R.string.BTN_Jars),
            getString(R.string.BTN_Bottles),
            getString(R.string.BTN_Сontainers),
            getString(R.string.BTN_Box),
            getString(R.string.BTN_Bottles),
            getString(R.string.BTN_GoodClothes),
            getString(R.string.BTN_BadClothes),
            getString(R.string.BTN_Battery),
            getString(R.string.BTN_Paper),
            getString(R.string.BTN_Technic)
        )
        val adaptermain: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, allGarbage)
        adaptermain.setDropDownViewResource(layout.simple_spinner_dropdown_item)
        spinnerGarbage.adapter = adaptermain

        btnAddGarbage.setOnClickListener {
            if (firebaseUser == null) {
                Toast.makeText(this, getString(R.string.noAccount), Toast.LENGTH_LONG).show()
                val mintent = Intent(this, LoginActivity::class.java)
                startActivity(mintent)
            } else {

            }
        }

    }
}