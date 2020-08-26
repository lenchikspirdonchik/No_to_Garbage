package spiridonov.no_to_garbage.account

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import spiridonov.no_to_garbage.MainActivity
import spiridonov.no_to_garbage.R


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
                                saveNameInDatabase(user)
                                createGarbageInDatabase(user)

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

    private fun createGarbageInDatabase(user: FirebaseUser) {
        var allGarbage = arrayOf(
            resources.getString(R.string.BTN_Jars),
            getString(R.string.BTN_Bottles),
            getString(R.string.BTN_Сontainers),
            getString(R.string.BTN_Box),
            getString(R.string.BTN_GoodClothes),
            getString(R.string.BTN_BadClothes),
            getString(R.string.BTN_Battery),
            getString(R.string.BTN_Paper),
            getString(R.string.BTN_Technic)
        )

        val firebaseDate = FirebaseDatabase.getInstance()
        val rootReference = firebaseDate.reference
        val garbageReference = rootReference.child("Users").child(user.uid).child("Garbage")
        for (i in 0..allGarbage.lastIndex) {
            val databaseReference = garbageReference.child(allGarbage[i])
            databaseReference.setValue("0")
        }

    }

    private fun saveNameInDatabase(user: FirebaseUser) {
        val firebaseDate = FirebaseDatabase.getInstance()
        val rootReference = firebaseDate.reference
        val nameReference = rootReference.child("Users").child(user.uid).child("Name")
        nameReference.setValue(txtSName.text.toString())
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
        if (txtSName.text.toString() == "") {
            txtSName.error = resources.getString(R.string.errorName)
            txtSName.requestFocus()
            return false

        }
        return true
    }
}