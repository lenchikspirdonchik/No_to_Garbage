package spiridonov.no_to_garbage

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

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
            val rs: ResultSet = st.executeQuery("select * from no2garbage")
            while (rs.next()) {
                Log.d(" ID ", rs.getString("id").toString())
                Log.d(" UUID ", rs.getString("uuid").toString())
                Log.d(" Date ", rs.getString("date").toString())
                Log.d(" Category ", rs.getString("category").toString())
                Log.d(" Amount ", rs.getString("amount").toString())
                Log.d(" END ", "---------------------")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            res = e.toString()
        }
        return res
    }

}