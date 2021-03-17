package spiridonov.no_to_garbage.homeMenu

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.account.LoginActivity
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class StatisticsActivity : AppCompatActivity() {
    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val mPieChart = findViewById<View>(R.id.piechart) as PieChart
        val mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth.currentUser
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
        val buffArray = arrayListOf<String>()
        val linearLayout = findViewById<LinearLayout>(R.id.staticLayout)
        val myinflater = LayoutInflater.from(this)

        if (firebaseUser != null) {
            this.url = String.format(this.url, this.host, this.port, this.database)
            val handler = Handler()
            for (i in 0..allGarbage.lastIndex) {
                val thread = Thread {
                    try {
                        Class.forName("org.postgresql.Driver")
                        val connection = DriverManager.getConnection(url, user, pass)
                        val st: Statement = connection.createStatement()

                        val rs: ResultSet =
                            st.executeQuery(" select SUM(amount) from no2garbage where category='${allGarbage[i]}' AND uuid='${firebaseUser.uid}'")

                        while (rs.next()) {
                            val dbdate: String = rs.getString(1).toString()
                            handler.post {
                                mPieChart.addPieSlice(
                                    PieModel(
                                        allGarbage[i],
                                        dbdate.toFloat(),
                                        Color.parseColor(colors[i])
                                    )
                                )
                                buffArray.add(allGarbage[i])
                                if (i == allGarbage.lastIndex) mPieChart.startAnimation()

                            }

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                thread.start()
                continue
            }

        } else {
            Toast.makeText(this, getString(R.string.noAccount), Toast.LENGTH_LONG).show()
            val mintent = Intent(this, LoginActivity::class.java)
            startActivity(mintent)
        }


        mPieChart.setOnItemFocusChangedListener { _Position: Int ->
            linearLayout.removeAllViews()
            val category: String
            if (buffArray.size < 8) category = "Бутылки"
            else category = buffArray[_Position]

            val uuid = mAuth.currentUser?.uid
            val handler = Handler()
            linearLayout.removeAllViews()
            val thread = Thread {
                try {
                    Class.forName("org.postgresql.Driver")
                    val connection = DriverManager.getConnection(url, user, pass)
                    val st: Statement = connection.createStatement()

                    if (uuid != null) {
                        val rs: ResultSet =
                            st.executeQuery(" select * from no2garbage where category='$category' AND uuid='$uuid' order by date desc")

                        while (rs.next()) {
                            val dbdate: String = rs.getString("date").toString()
                            val dbcategory = rs.getString("category").toString()
                            val dbamount = rs.getString("amount").toString()

                            handler.post {

                                val view =
                                    myinflater.inflate(R.layout.note_card, linearLayout, false)
                                val date = view.findViewById<TextView>(R.id.note_date)
                                val category = view.findViewById<TextView>(R.id.note_category)
                                val amount = view.findViewById<TextView>(R.id.note_amount)
                                date.text = dbdate
                                category.text = dbcategory
                                amount.text = dbamount
                                linearLayout.addView(view)

                            }
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
