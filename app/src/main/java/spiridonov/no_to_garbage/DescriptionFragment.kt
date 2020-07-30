package spiridonov.no_to_garbage

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_description.*

class DescriptionFragment : Fragment() {

    private lateinit var msp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_description, container, false)
        msp = this.requireActivity().getSharedPreferences("things", Context.MODE_PRIVATE)
        if (msp.contains("thing")) {
           val mainCategory = msp.getString("thing", "").toString()
            val firebaseDate = FirebaseDatabase.getInstance()
            val rootReference = firebaseDate.reference
            val garbageReference = rootReference.child("GarbageInformation").child(mainCategory)

            garbageReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    val headReference = garbageReference.child("head")
                    val bodyReference = garbageReference.child("body")

                    headReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            desc_head.text = snapshot.getValue(String::class.java)!!
                        }
                    })

                    bodyReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            desc_body.text = snapshot.getValue(String::class.java)!!
                        }
                    })
                }
            })

        }
        return root
    }
}