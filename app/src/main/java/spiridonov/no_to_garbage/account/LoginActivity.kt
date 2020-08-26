package spiridonov.no_to_garbage.account

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import spiridonov.no_to_garbage.R


class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var msp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        var firebaseUser = mAuth.currentUser
        if (firebaseUser != null) {
            val mintent = Intent()
            setResult(Activity.RESULT_OK, mintent)
            finish()
        }
        btnLogin.setOnClickListener {
            if (handlelogin()) {
                 PbarLogin.visibility = View.VISIBLE
                val siqnIn = Thread(Runnable {
                    mAuth.signInWithEmailAndPassword(
                        txtEmail.text.toString(),
                        txtPassword.text.toString()
                    ).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            firebaseUser = mAuth.currentUser
                            Log.d("Log", "${task.result.toString()}")
                            Toast.makeText(
                                this,
                                "${getString(R.string.successSiqnIn)}\n${firebaseUser!!.email.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val mintent = Intent()
                            setResult(Activity.RESULT_OK, mintent)
                            finish()
                        } else {
                            PbarLogin.visibility = View.INVISIBLE
                            Log.d("Log", "${task.exception}")
                            btnLogin.error = "Invalid data"
                            btnLogin.requestFocus()
                        }
                    }
                })
                siqnIn.start()

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
            txtEmail.error = resources.getString(R.string.errorEmail)
            txtEmail.requestFocus()
            return false
        }
        if (!checkPasswordIsValid(txtPassword.text.toString())) {
            txtPassword.error = resources.getString(R.string.errorPassword)
            txtPassword.requestFocus()
            return false
        }
        return true
    }

}