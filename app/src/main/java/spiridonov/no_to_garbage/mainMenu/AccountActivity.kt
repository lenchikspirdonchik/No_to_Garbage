package spiridonov.no_to_garbage.mainMenu

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_account.*
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.account.LoginActivity
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*


class AccountActivity : AppCompatActivity() {
    private lateinit var nameReference: DatabaseReference
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth.currentUser
        val firebaseDate = FirebaseDatabase.getInstance()
        val rootReference = firebaseDate.reference

        if (firebaseUser == null) {
            btn_null.isEnabled = false
            btn_signOut.isEnabled = false
            val mintent = Intent(this, LoginActivity::class.java)
            startActivityForResult(mintent, 1)
        } else {
            val name = Thread {
                nameReference = rootReference.child("Users").child(firebaseUser.uid).child("Name")
                nameReference.addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value: String? = dataSnapshot.getValue(String::class.java)
                        if (value != null)
                            txtName.text = "Добрый день, " +
                                    value +
                                    "\nВаша почта: ${firebaseUser.email}"
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            }

            val handler = Handler()
            val garbage = Thread {

                this.url = String.format(this.url, this.host, this.port, this.database);
                try {
                    Class.forName("org.postgresql.Driver");
                    val connection = DriverManager.getConnection(url, user, pass);
                    val st: Statement = connection.createStatement()
                    val rs: ResultSet =
                        st.executeQuery("select category, SUM(amount) from no2garbage where uuid = '${firebaseUser.uid}' group by category")

                    while (rs.next()) {
                        val dbCategory: String = rs.getString("category").toString()
                        val dbSum = rs.getString("sum").toString()
                        handler.post {
                            txtGarbage.text = "${txtGarbage.text}\n$dbCategory:   $dbSum"
                        }

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            name.start()
            garbage.start()
            btn_null.isEnabled = true
            btn_signOut.isEnabled = true

            btn_null.setOnClickListener {
                deleteDB(firebaseUser.uid)
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
                for (i in 0..allGarbage.lastIndex) {
                    val category = allGarbage[i]
                    val startDate = Calendar.getInstance()
                    val day = startDate.get(Calendar.DAY_OF_MONTH).toString()
                    val month = (startDate.get(Calendar.MONTH) + 1).toString()
                    val year = startDate.get(Calendar.YEAR).toString()
                    val thread = Thread {
                        try {
                            Class.forName("org.postgresql.Driver");
                            val connection = DriverManager.getConnection(url, user, pass);
                            val st: Statement = connection.createStatement()
                            st.execute(
                                " insert into no2garbage (uuid, date, category, amount)\n" +
                                        "VALUES ('${firebaseUser.uid}', date('$month/$day/$year'), '$category', 1);"
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    thread.start()

                }
                recreate()
            }


            btn_delete.setOnClickListener {
                val pDialog = SweetAlertDialog(this, SweetAlertDialog.BUTTON_POSITIVE)
                pDialog.progressHelper.barColor = Color.parseColor("#264599")
                pDialog.titleText = "Вы точно хотите удалить аккаунт?"
                pDialog.contentText =
                    "Удалится вся ваша статистика. Это действие будет не отменить!"
                pDialog.confirmText = "Отмена"
                pDialog.cancelText = "Удалить"
                pDialog.progressHelper.rimColor = Color.parseColor("#264599")
                pDialog.setCancelable(false)
                pDialog.setConfirmClickListener {
                    pDialog.dismiss()
                }
                pDialog.setCancelClickListener {
                    recreate()
                    deleteDB(firebaseUser.uid)
                    val userReference = rootReference.child("Users").child(firebaseUser.uid)
                    userReference.removeValue()
                    firebaseUser.delete()
                }
                pDialog.progressHelper.spin()
                pDialog.show()
            }


            btn_signOut.setOnClickListener {
                mAuth.signOut()

                recreate()

            }
        }


    }

    private fun deleteDB(uid: String) {
        val thread = Thread {
            this.url = String.format(this.url, this.host, this.port, this.database);
            try {
                Class.forName("org.postgresql.Driver");
                val connection = DriverManager.getConnection(url, user, pass);
                val st: Statement = connection.createStatement()
                st.execute(
                    "DELETE FROM no2garbage\n" +
                            "WHERE  uuid = '$uid';"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        thread.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            recreate()
        }
    }
}
