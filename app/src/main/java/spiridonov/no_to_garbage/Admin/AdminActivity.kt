package spiridonov.no_to_garbage.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_admin.*
import spiridonov.no_to_garbage.R


class AdminActivity : AppCompatActivity() {
    val firebaseDate = FirebaseDatabase.getInstance()
    val rootReference = firebaseDate.reference
    private lateinit var selectedItem: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mintent: Intent? = Intent(this, ValidateActivity::class.java)
        startActivityForResult(mintent, 1)
        setContentView(R.layout.activity_admin)
        spinnerGarbage.isEnabled = false
        editText.isEnabled = false
        textView.isEnabled = false
        btnSaveText.isEnabled = false
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
        adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGarbage.adapter = adaptermain

        spinnerGarbage.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedItem = parent.getItemAtPosition(position).toString()
                Log.d("spinner", selectedItem)
                val garbageReference = rootReference.child("GarbageInformation").child(selectedItem)
                val bodyReference = garbageReference.child("body")
                bodyReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val txt: String? = snapshot.getValue(String::class.java)
                        if (txt != null) editText.setText(txt.replace("_n", "\n"))
                    }
                })

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSaveText.setOnClickListener {
            saveToDatabase()
        }

    }

    private fun saveToDatabase() {
        if (editText.isEnabled) {
            val garbageReference = rootReference.child("GarbageInformation").child(selectedItem)
            val bodyReference = garbageReference.child("body")
            bodyReference.setValue(editText.text.toString().replace("\n", "_n"))
            Toast.makeText(this, "Готово!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            spinnerGarbage.visibility = View.VISIBLE
            editText.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
            btnSaveText.visibility = View.VISIBLE
            spinnerGarbage.isEnabled = true
            editText.isEnabled = true
            textView.isEnabled = true
            btnSaveText.isEnabled = true
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

    override fun onStop() {
        super.onStop()
        saveToDatabase()
    }
}