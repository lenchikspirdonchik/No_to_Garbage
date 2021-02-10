package spiridonov.no_to_garbage

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.sql.*

class DatabaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_database, container, false)
        val connect2Data = ConnectMySQL()
        connect2Data.execute("")


        return root
    }


}


private class ConnectMySQL : AsyncTask<String, Void, String>() {
    var res = ""

    override fun doInBackground(vararg params: String?): String {
        val url = "jdbc:mysql://198.199.73.149:3306/spiridonovproduction"
        val user = "spiridonovproduction"
        val password = "H+4ynXm20/5Yf-T"
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val con: Connection = DriverManager.getConnection(url, user, password)
            println("Databaseection success")
            var result = "Database Connection Successful\n"
            val st: Statement = con.createStatement()
            val rs: ResultSet = st.executeQuery("select category from no2garbage")
            val rsmd: ResultSetMetaData = rs.getMetaData()
            while (rs.next()) {
                result += rs.getString(1).toString().toString() + "\n"

            }
            res = result
            Log.d("Database", res)
        } catch (e: Exception) {
            e.printStackTrace()
            res = e.toString()
        }
        return res
    }

}