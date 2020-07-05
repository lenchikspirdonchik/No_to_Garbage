package spiridonov.no_to_garbage

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    val LOG_TAG = "SignUpActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()


        btnSignUp.setOnClickListener {

            Log.d(LOG_TAG, "setOnClickListener")
            if (handlelogin()) {
                Log.d(LOG_TAG, "handlelogin true")
                mAuth.createUserWithEmailAndPassword(
                    txtSEmail.text.toString(),
                    txtSPassword.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            if (user != null) {
                                Toast.makeText(
                                    this,
                                    "${getString(R.string.successSiqnUp)}\n${user.email.toString()}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                user.sendEmailVerification()
                            }
                        } else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

//2003.leonid2003@gmail.com
            } else Log.d(LOG_TAG, "handlelogin false")
        }
    }


    private fun handlelogin(): Boolean {
        if (!checkEmailIsValid(txtSEmail.text.toString())) {
            Log.d(LOG_TAG, "checkEmailIsValid false")
            Snackbar.make(
                this.findViewById(android.R.id.content),
                resources.getString(R.string.errorEmail),
                Snackbar.LENGTH_LONG
            )
            return false
        }
        if (!checkPasswordIsValid(txtSPassword.text.toString())
            || !txtSPassword.text.toString().equals(txtSAgainPassword.text.toString())
        ) {
            Log.d(LOG_TAG, "checkPasswordIsValid false")
            Snackbar.make(
                this.findViewById(android.R.id.content),
                resources.getString(R.string.errorPassword),
                Snackbar.LENGTH_LONG
            )
            return false
        }
        return true
    }
}