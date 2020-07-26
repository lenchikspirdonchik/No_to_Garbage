package spiridonov.no_to_garbage

import android.R.layout
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
            getString(R.string.BTN_GoodClothes),
            getString(R.string.BTN_BadClothes),
            getString(R.string.BTN_Battery),
            getString(R.string.BTN_Paper),
            getString(R.string.BTN_Technic)
        )
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
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
                if (editTextNumber.text.toString() != "") {
                    Log.d("TAG", "Value is: ${spinnerGarbage.selectedItem}")
                    var flag = true
                    val garbageReference =
                        rootReference.child("Users").child(firebaseUser.uid).child("Garbage")
                            .child(spinnerGarbage.selectedItem.toString())
                    garbageReference.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val kolvoString = snapshot.getValue(String::class.java)!!
                            if (flag && kolvoString != "") {
                                var kolvo = kolvoString.toInt()
                                kolvo += editTextNumber.text.toString().toInt()
                                garbageReference.setValue(kolvo.toString())
                                flag = false
                            }
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                resources.getString(R.string.refreshInformation),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(resources.getString(R.string.undo)) {
                                    val kolvoString2 = snapshot.getValue(String::class.java)!!
                                    if (kolvoString2 != "") {
                                        var kolvo = kolvoString2.toInt()
                                        kolvo -= editTextNumber.text.toString().toInt()
                                        garbageReference.setValue(kolvo.toString())
                                        flag = false
                                    }
                                }.setActionTextColor(Color.RED).show()
                        }
                    })

                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}