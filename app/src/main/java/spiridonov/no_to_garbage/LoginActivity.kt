package spiridonov.no_to_garbage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()




        btnLogin.setOnClickListener {
            if (handlelogin()) {
                mAuth.signInWithEmailAndPassword(
                    txtEmail.text.toString(),
                    txtPassword.text.toString()
                )
                    .addOnCompleteListener(
                        this
                    ) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            if (user != null) {
                                Toast.makeText(
                                    this,
                                    "${getString(R.string.successSiqnIn)}\n${user.email.toString()}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val mintent = Intent()
                                mintent.putExtra("key_email", user.email)
                                setResult(Activity.RESULT_OK, mintent)
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
            }

        }
        txtNoAcc.setOnClickListener {
            val rotate: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
            txtNoAcc.startAnimation(rotate)
            val mintent = Intent(this, SignUpActivity::class.java)
            startActivity(mintent)
        }
    }

    private fun handlelogin(): Boolean {
        if (!checkEmailIsValid(txtEmail.text.toString())) {
            Snackbar.make(
                this.findViewById(android.R.id.content),
                resources.getString(R.string.errorEmail),
                Snackbar.LENGTH_LONG
            )
            return false
        }
        if (!checkPasswordIsValid(txtPassword.text.toString())) {
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