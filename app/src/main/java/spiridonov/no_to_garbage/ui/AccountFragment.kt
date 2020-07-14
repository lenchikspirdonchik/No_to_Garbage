package spiridonov.no_to_garbage.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import spiridonov.no_to_garbage.LoginActivity
import spiridonov.no_to_garbage.R


class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_account, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val btn_signOu = root.findViewById<Button>(R.id.btn_signOut)
        val textView = root.findViewById<TextView>(R.id.textView4)
        val firebaseUser = mAuth.currentUser
        val firebaseDate = FirebaseDatabase.getInstance()
        val rootReference = firebaseDate.reference


        if (firebaseUser == null) {
            val mintent: Intent? = Intent(context, LoginActivity::class.java)
            startActivityForResult(mintent, 1)
        } else {
            Log.d("Log", "from account page ${firebaseUser.email.toString()}")
            val nameReference = rootReference.child("Users").child(firebaseUser.uid).child("Name")

            nameReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value =
                        dataSnapshot.getValue(String::class.java)!!
                    Log.d("TAG", "Value is: $value")
                    textView.text = "добрый день, " +
                            "$value" +
                            "\nyour email: ${firebaseUser.email}"
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })


        }
        btn_signOu.setOnClickListener {
            mAuth.signOut()

            activity?.recreate()
        }
        return root

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            activity?.recreate()
        }


    }
}
