package spiridonov.no_to_garbage

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()


        btnSignUp.setOnClickListener {
            if (handlelogin()) {
                mAuth.createUserWithEmailAndPassword(
                    txtSEmail.text.toString(),
                    txtSPassword.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                        } else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }


            }
        }
    }


    private fun handlelogin(): Boolean {
        if (!checkEmailIsValid(txtSEmail.toString())) {
            Snackbar.make(
                this.findViewById(android.R.id.content),
                resources.getString(R.string.errorEmail),
                Snackbar.LENGTH_LONG
            )
            return false
        }
        if (!checkPasswordIsValid(txtSPassword.toString())
            || !txtSPassword.text.toString().equals(txtSAgainPassword.text.toString())
        ) {
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