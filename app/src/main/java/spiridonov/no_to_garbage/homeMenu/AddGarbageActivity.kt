package spiridonov.no_to_garbage.homeMenu

import android.R.layout
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.account.LoginActivity
import java.sql.DriverManager
import java.sql.Statement
import java.util.*


class AddGarbageActivity : AppCompatActivity() {
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"
    private val mAuth = FirebaseAuth.getInstance()
    private val firebaseUser = mAuth.currentUser
    private val firebaseDate = FirebaseDatabase.getInstance()
    private val rootReference = firebaseDate.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_garbage)
        val allGarbage = arrayOf(
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

        if (firebaseUser == null) {
            Toast.makeText(this, getString(R.string.noAccount), Toast.LENGTH_LONG).show()
            val mintent = Intent(this, LoginActivity::class.java)
            startActivity(mintent)
        }
        btnAddGarbage.setOnClickListener {
            if (firebaseUser != null) {
                val numberGar = editTextNumber.text.toString()
                var oldNumberGar: String
                this.url = String.format(this.url, this.host, this.port, this.database);
                if (numberGar != "") {
                    Save2SQL(
                        spinnerGarbage.selectedItem.toString(),
                        numberGar.toInt(),
                        firebaseUser.uid.toString()
                    )
                    var flag = true
                    val garbageReference =
                        rootReference.child("Users").child(firebaseUser.uid).child("Garbage")
                            .child(spinnerGarbage.selectedItem.toString())

                    garbageReference.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val kolvoString = snapshot.getValue(String::class.java)!!
                            oldNumberGar = kolvoString
                            if (flag && kolvoString != "") {
                                var kolvo = kolvoString.toInt()
                                kolvo += numberGar.toInt()
                                garbageReference.setValue(kolvo.toString())
                                flag = false
                            }


                            Snackbar.make(
                                findViewById(android.R.id.content),
                                resources.getString(R.string.refreshInformation),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(resources.getString(R.string.undo)) {
                                    garbageReference.setValue(oldNumberGar)
                                    flag = false

                                }.setActionTextColor(Color.RED).show()


                            Toast.makeText(
                                applicationContext,
                                getString(R.string.done),
                                Toast.LENGTH_LONG
                            ).show()
                            editTextNumber.setText(getString(R.string.done))
                            editTextNumber.isEnabled = false
                            btnAddGarbage.isEnabled = false

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            recreate()
        }
    }


    private fun Save2SQL(category: String, amount: Int, uuid: String) {
        val startDate = Calendar.getInstance()
        val day = startDate.get(Calendar.DAY_OF_MONTH).toString()
        val month = (startDate.get(Calendar.MONTH) + 1).toString()
        val year = startDate.get(Calendar.YEAR).toString()


        val thread = Thread {
            try {
                Class.forName("org.postgresql.Driver");
                val connection = DriverManager.getConnection(url, user, pass);
                val st: Statement = connection.createStatement()
                st.execute(
                    " insert into no2garbage (uuid, date, category, amount)\n" +
                            "VALUES ('$uuid', date('$month/$day/$year'), '$category', $amount);"
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        thread.start()
    }
}