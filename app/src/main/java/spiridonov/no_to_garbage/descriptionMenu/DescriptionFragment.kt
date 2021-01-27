package spiridonov.no_to_garbage.descriptionMenu

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_description.*
import spiridonov.no_to_garbage.R
import java.io.File


class DescriptionFragment : Fragment() {
    private lateinit var mStorageRef: StorageReference
    private lateinit var msp: SharedPreferences
    private lateinit var mainCategory: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_description, container, false)
        msp = this.requireActivity().getSharedPreferences("things", Context.MODE_PRIVATE)
        if (msp.contains("thing")) {
            mainCategory = msp.getString("thing", "").toString()
        }

        val threadDescription = Thread {

            val firebaseDate = FirebaseDatabase.getInstance()
            val rootReference = firebaseDate.reference
            val garbageReference = rootReference.child("GarbageInformation").child(mainCategory)

            val headReference = garbageReference.child("head")
            val bodyReference = garbageReference.child("body")

            headReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    val txt: String? = snapshot.getValue(String::class.java)
                    if (txt != null) desc_head.text = txt
                }
            })

            bodyReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    val txt: String? = snapshot.getValue(String::class.java)
                    if (txt != null) desc_body.text = txt.replace("_n", "\n")
                }
            })


        }

        threadDescription.start()
        val gallery = root.findViewById<LinearLayout>(R.id.trueGallery)
        val myinflater = LayoutInflater.from(context)

        mStorageRef = FirebaseStorage.getInstance().reference
        val garbageRef: StorageReference =
            mStorageRef.child("Garbage").child(mainCategory)


        garbageRef.listAll().addOnSuccessListener { result ->
            result.items.forEach { imageRef ->
                val view = myinflater.inflate(R.layout.gallery_item, gallery, false)
                val text = view.findViewById<TextView>(R.id.txtGallery)
                text.text = ""
                val img = view.findViewById<ImageView>(R.id.imgGallery)
                img.setOnClickListener {
                    it
                    Toast.makeText(context, "Click on ${it.id}!", Toast.LENGTH_SHORT).show()
                }
                
                val localFile = File.createTempFile("images", "png")
                imageRef.getFile(localFile)
                    .addOnSuccessListener {
                        // Successfully downloaded data to local file
                        val bitmap = BitmapFactory.decodeFile(localFile.path)
                        img.setImageBitmap(bitmap)

                    }.addOnFailureListener {
                        // Handle failed download
                        // ...

                    }.addOnProgressListener {
                        Log.d("Progress", "Download")
                    }

                gallery.addView(view)
            }
        }






        return root
    }
}