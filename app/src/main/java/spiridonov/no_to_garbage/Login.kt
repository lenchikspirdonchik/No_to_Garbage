package spiridonov.no_to_garbage

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {
    var user: FirebaseUser? = null
    fun loginBackend(activity: Activity, email: String, password: String): FirebaseUser? {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            activity
        ) { task ->
            if (task.isSuccessful) {
                user = mAuth.currentUser
                Log.d("Log", "from login. Task is successful. User =  ${user?.email.toString()}")

            }

        }

        return user
    }
}

