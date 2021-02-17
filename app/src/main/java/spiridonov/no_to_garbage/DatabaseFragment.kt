package spiridonov.no_to_garbage

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement


class DatabaseFragment : Fragment() {

    private val host = "ec2-108-128-104-50.eu-west-1.compute.amazonaws.com"
    private val database = "dvvl3t4j8k5q7"
    private val port = 5432
    private val user = "mpzdfkfaoiwywz"
    private val pass = "c37ce7e3b99d480a04b8943b89ba6e7abb94cb86c56bfa4c6ace4fab4cbc287d"
    private var url = "jdbc:postgresql://%s:%d/%s"
    private var status = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_database, container, false)

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
        val spinner = root.findViewById<Spinner>(R.id.spinnerGarbageDB)
        val adaptermain: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                allGarbage
            )
        adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptermain
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                buildCard(root, allGarbage[selectedItemPosition])

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return root
    }


    private fun buildCard(root: View, category: String) {
        val mAuth = FirebaseAuth.getInstance()
        val uuid = mAuth.currentUser?.uid
        this.url = String.format(this.url, this.host, this.port, this.database);

        val handler = Handler()
        val linearLayout = root.findViewById<LinearLayout>(R.id.myLin)
        val myinflater = LayoutInflater.from(context)
        linearLayout.removeAllViews()

        val thread = Thread {
            try {
                Class.forName("org.postgresql.Driver");
                val connection = DriverManager.getConnection(url, user, pass);
                status = true
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


