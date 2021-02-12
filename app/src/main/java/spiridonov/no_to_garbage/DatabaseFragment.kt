package spiridonov.no_to_garbage

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*


class DatabaseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_database, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val uuid = mAuth.currentUser?.uid


        val url = "jdbc:mysql://198.199.73.149:3306/spiridonovproduction"
        val user = "spiridonovproduction"
        val password = "H+4ynXm20/5Yf-T"

        val handler = Handler()
        val recyclerView = root.findViewById<LinearLayout>(R.id.myLin)
        val myinflater = LayoutInflater.from(context)


        val p = Properties()
        p.setProperty("user", user)
        p.setProperty("password", password)
        p.setProperty("useUnicode", "true")
        p.setProperty("characterEncoding", "cp1251")
        val thread = Thread {
            try {
                Class.forName("com.mysql.jdbc.Driver")
                val con: Connection = DriverManager.getConnection(url, p)
                val st: Statement = con.createStatement()

                if (uuid != null) {
                    val rs: ResultSet =
                        st.executeQuery(" select * from no2garbage where category='Батарейки' AND uuid='${uuid}' order by date desc")

                    while (rs.next()) {
                        val dbdate: String = rs.getString("date").toString()
                        val dbcategory = rs.getString("category").toString()
                        val dbamount = rs.getString("amount").toString()

                        handler.post {
                            val view =
                                myinflater.inflate(R.layout.note_card, recyclerView, false)
                            val date = view.findViewById<TextView>(R.id.note_date)
                            val category = view.findViewById<TextView>(R.id.note_category)
                            val amount = view.findViewById<TextView>(R.id.note_amount)
                            date.text = dbdate
                            category.text = dbcategory
                            amount.text = dbamount
                            recyclerView.addView(view)

                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
        thread.start()


        return root
    }


}


