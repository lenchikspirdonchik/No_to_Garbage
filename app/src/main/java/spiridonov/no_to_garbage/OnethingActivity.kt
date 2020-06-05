package spiridonov.no_to_garbage


import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class OnethingActivity : AppCompatActivity() {

    private val TAG = "DocSnippets"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mintent = intent
        var mainCategory: String = mintent.extras?.getString("KEY_CATEGORY").toString()
        setContentView(R.layout.activity_onething)
        val txt = findViewById<TextView>(R.id.textView)
        val db = Firebase.firestore
        db.collection(mainCategory).get().addOnSuccessListener { result ->
            for (document in result) {
                txt.text = "${document.getString("Head")}\n${document.getString("Body")}"
            }
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


    }

}
