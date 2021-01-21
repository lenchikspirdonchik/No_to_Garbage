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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_description, container, false)

        val gallery = root.findViewById<LinearLayout>(R.id.trueGallery)
        val inflater = LayoutInflater.from(context)
        val threadPhoto = Thread {
            for (i in 1..14) {
                val view = inflater.inflate(R.layout.gallery_item, gallery, false)
                val text = view.findViewById<TextView>(R.id.txtGallery)
                text.text = ""
                val img = view.findViewById<ImageView>(R.id.imgGallery)
                img.setOnClickListener {
                    Toast.makeText(context, "Click!", Toast.LENGTH_SHORT).show()
                }
                mStorageRef = FirebaseStorage.getInstance().reference
                val riversRef: StorageReference = mStorageRef.child("$i.png")
                Log.d("photo", "$i.png")
                val localFile = File.createTempFile("images", "png")
                riversRef.getFile(localFile)
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


        val threadDescription = Thread {
            msp = this.requireActivity().getSharedPreferences("things", Context.MODE_PRIVATE)
            if (msp.contains("thing")) {
                val mainCategory = msp.getString("thing", "").toString()
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
        }
        threadPhoto.start()
        threadDescription.start()
        return root
    }
}