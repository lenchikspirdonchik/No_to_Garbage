package spiridonov.no_to_garbage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_validate.*

class ValidateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate)
        val firebaseDate = FirebaseDatabase.getInstance()
        val rootReference = firebaseDate.reference
        val passReference = rootReference.child("Password")
        var password = ""
        passReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                password = snapshot.getValue(String::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        btnPass.setOnClickListener {
            if (txtPass.text.toString().equals(password)) {
                val mintent = Intent()
                setResult(Activity.RESULT_OK, mintent)
                finish()
            }
        }
    }
}