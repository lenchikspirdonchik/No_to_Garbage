package spiridonov.no_to_garbage.mainMenu

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import spiridonov.no_to_garbage.Admin.AdminActivity
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.account.LoginActivity
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement


class AccountFragment : Fragment() {
    private lateinit var nameReference: DatabaseReference
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.fragment_account, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val btn_signOu = root.findViewById<Button>(R.id.btn_signOut)
        val btn_null = root.findViewById<Button>(R.id.btn_null)
        val btn_del = root.findViewById<Button>(R.id.btn_delete)
        val textView = root.findViewById<TextView>(R.id.txtName)
        val txtGarbage = root.findViewById<TextView>(R.id.txtGarbage)
        val firebaseUser = mAuth.currentUser
        val firebaseDate = FirebaseDatabase.getInstance()
        val rootReference = firebaseDate.reference

        if (firebaseUser == null) {
            btn_null.isEnabled = false
            btn_signOu.isEnabled = false
            val mintent: Intent? = Intent(context, LoginActivity::class.java)
            startActivityForResult(mintent, 1)
        } else {
            val name = Thread {
                nameReference = rootReference.child("Users").child(firebaseUser.uid).child("Name")
                nameReference.addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value: String? = dataSnapshot.getValue(String::class.java)
                        if (value != null)
                            textView.text = "добрый день, " +
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
            btn_signOu.isEnabled = true

            btn_null.setOnClickListener {
                deleteDB(firebaseUser.uid)
                activity?.recreate()
            }


            btn_del.setOnClickListener {
                val pDialog = SweetAlertDialog(context, SweetAlertDialog.BUTTON_POSITIVE)
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
                    activity?.recreate()
                    deleteDB(firebaseUser.uid)
                    firebaseUser.delete()

                }
                pDialog.progressHelper.spin()
                pDialog.show()
            }


            btn_signOu.setOnClickListener {
                mAuth.signOut()

                activity?.recreate()

            }
        }


        return root

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
            activity?.recreate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.admin_account, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_admin -> {
                val mintent = Intent(context, AdminActivity::class.java)
                startActivity(mintent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
