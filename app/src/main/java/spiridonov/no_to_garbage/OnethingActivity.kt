package spiridonov.no_to_garbage


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class OnethingActivity : AppCompatActivity() {

    private val TAG = "DocSnippets"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mintent = intent
        var mainCategory: String = mintent.extras?.getString("KEY_CATEGORY").toString()
        setContentView(R.layout.activity_onething)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_description, R.id.navigation_Map
        )
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)


        // val txt = findViewById<TextView>(R.id.textView)
        val db = Firebase.firestore
        db.collection(mainCategory).get().addOnSuccessListener { result ->
            for (document in result) {
                //txt.text = "${document.getString("Head")}\n${document.getString("Body")}"
            }
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


    }

}
