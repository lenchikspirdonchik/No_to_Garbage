package spiridonov.no_to_garbage

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                            if (user != null) {
                                Toast.makeText(
                                    this,
                                    "${getString(R.string.successSiqnUp)}\n${user.email.toString()}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                user.sendEmailVerification()
                                val mintent: Intent? =
                                    Intent(applicationContext, MainActivity::class.java)
                                startActivity(mintent)
                                finish()
                            }
                        } else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

            }
        }
    }

    private fun handlelogin(): Boolean {
        if (!checkEmailIsValid(txtSEmail.text.toString())) {
            txtSEmail.error = resources.getString(R.string.errorEmail)
            txtSEmail.requestFocus()
            return false
        }
        if (!checkPasswordIsValid(txtSPassword.text.toString())
            || !txtSPassword.text.toString().equals(txtSAgainPassword.text.toString())
        ) {
            txtSPassword.error = resources.getString(R.string.errorPassword)
            txtSPassword.requestFocus()
            return false
        }
        return true
    }
}