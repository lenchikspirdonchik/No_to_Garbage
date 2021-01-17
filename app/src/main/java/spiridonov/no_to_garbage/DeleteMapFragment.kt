package spiridonov.no_to_garbage

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class DeleteMapFragment : Fragment() {
    private lateinit var mStorageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_delete_map, container, false)
        val allGarbage = arrayOf(
            getString(R.string.BTN_Jars),
            getString(R.string.BTN_Bottles),
            getString(R.string.BTN_Сontainers),
            getString(R.string.BTN_Box),
            getString(R.string.BTN_GoodClothes),
            getString(R.string.BTN_BadClothes),
            getString(R.string.BTN_Battery),
            getString(R.string.BTN_Paper),
            getString(R.string.BTN_Technic)
        )
        val img = root.findViewById<ImageView>(R.id.imgView)
        mStorageRef = FirebaseStorage.getInstance().reference
        val riversRef: StorageReference = mStorageRef.child("battery.png")
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




        return root
    }

}