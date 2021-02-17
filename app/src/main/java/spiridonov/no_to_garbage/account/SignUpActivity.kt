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
import java.sql.DriverManager
import java.sql.Statement
import java.util.*


class SignUpActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val dbuser = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"
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
        val allGarbage = arrayOf(
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

        this.url = String.format(this.url, this.host, this.port, this.database);
        for (i in 0..allGarbage.lastIndex) {
            val category = allGarbage[i]
            val startDate = Calendar.getInstance()
            val day = startDate.get(Calendar.DAY_OF_MONTH).toString()
            val month = (startDate.get(Calendar.MONTH) + 1).toString()
            val year = startDate.get(Calendar.YEAR).toString()
            val thread = Thread {
                try {
                    Class.forName("org.postgresql.Driver");
                    val connection = DriverManager.getConnection(url, dbuser, pass);
                    val st: Statement = connection.createStatement()
                    st.execute(
                        " insert into no2garbage (uuid, date, category, amount)\n" +
                                "VALUES ('${user.uid}', date('$month/$day/$year'), '$category', 1);"
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            thread.start()

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