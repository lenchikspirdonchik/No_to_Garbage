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
import com.google.firebase.database.*
import spiridonov.no_to_garbage.LoginActivity
import spiridonov.no_to_garbage.R


class AccountFragment : Fragment() {
    private lateinit var nameReference: DatabaseReference
    private lateinit var garbageReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            nameReference = rootReference.child("Users").child(firebaseUser.uid).child("Name")
            garbageReference = rootReference.child("Users").child(firebaseUser.uid).child("Garbage")

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
                garbageReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in 0..allGarbage.lastIndex) {
                            val databaseReference = garbageReference.child(allGarbage[i])
                            databaseReference.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {}
                                override fun onDataChange(datasnapshot: DataSnapshot) {
                                    val garbage = datasnapshot.getValue(String::class.java)!!
                                    textView.text = "${textView.text}\n ${allGarbage[i]} : $garbage"

                                }

                            })
                        }
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
