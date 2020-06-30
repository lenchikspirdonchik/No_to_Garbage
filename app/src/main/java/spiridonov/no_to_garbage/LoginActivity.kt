package spiridonov.no_to_garbage

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        btnLogin.setOnClickListener {
            if (handlelogin()) {

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
        if (!checkEmailIsValid(txtEmail.toString())) {
            Snackbar.make(
                this.findViewById(android.R.id.content),
                resources.getString(R.string.errorEmail),
                Snackbar.LENGTH_LONG
            )
            return false
        }
        if (!checkPasswordIsValid(txtPassword.toString())) {
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