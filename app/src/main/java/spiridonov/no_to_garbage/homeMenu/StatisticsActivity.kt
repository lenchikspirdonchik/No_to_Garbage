package spiridonov.no_to_garbage.homeMenu

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_statistics.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.account.LoginActivity
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val mPieChart = findViewById<View>(R.id.piechart) as PieChart
        val mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth.currentUser
        val firebaseDate = FirebaseDatabase.getInstance()
        val rootReference = firebaseDate.reference
        val allGarbage = arrayOf(
            getString(R.string.BTN_Jars),
            getString(R.string.BTN_Bottles),
            getString(R.string.BTN_Сontainers),
            getString(R.string.BTN_Box),
            getString(R.string.BTN_GoodClothes),
            getString(R.string.BTN_BadClothes),
            getString(R.string.BTN_Battery),
            getString(R.string.BTN_Paper),
            getString(R.string.BTN_Technic)
        )
        val colors = arrayOf(
            "#FE6DA8",
            "#56B7F1",
            "#CDA67F",
            "#FED70E",
            "#45D09E",
            "#00848C",
            "#41B619",
            "#8EAF0C",
            "#FFD600"
        )


        if (firebaseUser != null) {
            val garbageReference =
                rootReference.child("Users").child(firebaseUser.uid).child("Garbage")
            for (i in 0..allGarbage.lastIndex) {
                val databaseReference =
                    garbageReference.child(allGarbage[i])
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val garbage = snapshot.getValue(String::class.java)
                        if (garbage != null) {
                            mPieChart.addPieSlice(
                                PieModel(
                                    allGarbage[i],
                                    garbage.toFloat(),
                                    Color.parseColor(colors[i])
                                )
                            )

                            if (i == allGarbage.size - 1) mPieChart.startAnimation()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

        } else {
            Toast.makeText(this, getString(R.string.noAccount), Toast.LENGTH_LONG).show()
            val mintent = Intent(this, LoginActivity::class.java)
            startActivity(mintent)
        }


        mPieChart.setOnItemFocusChangedListener { _Position: Int ->

            Log.d(" mPieChart ", mPieChart.emptyDataText.toString())

            val category = allGarbage[mPieChart.currentItem]
            Toast.makeText(this, category, Toast.LENGTH_SHORT).show()
            val uuid = mAuth.currentUser?.uid
            val handler = Handler()
            val url = "jdbc:mysql://198.199.73.149:3306/spiridonovproduction"
            val p = Properties()
            p.setProperty("user", "spiridonovproduction")
            p.setProperty("password", "H+4ynXm20/5Yf-T")
            p.setProperty("useUnicode", "true")
            p.setProperty("characterEncoding", "cp1251")

            val thread = Thread {
                var res = ""
                try {
                    Class.forName("com.mysql.jdbc.Driver")
                    val con: Connection = DriverManager.getConnection(url, p)
                    var result = ""
                    val st: Statement = con.createStatement()
                    val rs: ResultSet =
                        st.executeQuery("select * from no2garbage where category='${category}' AND uuid='${uuid}'")

                    while (rs.next()) {

                        Log.d(" Date ", rs.getString("date").toString())
                        Log.d(" Category ", rs.getString("category").toString())
                        Log.d(" Amount ", rs.getString("amount").toString())
                        Log.d(" END ", "---------------------")


                        result += " Дата: ${rs.getString("date").toString()}\n"
                        result += " Категория: ${rs.getString("category").toString()}\n"
                        result += " Количество: ${rs.getString("amount").toString()}\n"
                        result += "---------------------\n"
                    }
                    res = result

                } catch (e: Exception) {
                    e.printStackTrace()
                    res = e.toString()
                }

                handler.post {
                    textViewStatistics.text = res
                }


            }
            thread.start()

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
