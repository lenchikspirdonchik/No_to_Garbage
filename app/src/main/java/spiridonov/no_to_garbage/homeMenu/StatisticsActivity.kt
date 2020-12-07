package spiridonov.no_to_garbage.homeMenu

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import spiridonov.no_to_garbage.R
import spiridonov.no_to_garbage.account.LoginActivity

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
            for (i in allGarbage.indices) {
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
            // Toast.makeText(this, allGarbage[_Position], Toast.LENGTH_LONG).show()

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
