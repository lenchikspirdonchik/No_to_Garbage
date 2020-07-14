package spiridonov.no_to_garbage.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import spiridonov.no_to_garbage.LoginActivity
import spiridonov.no_to_garbage.R


class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_account, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val btn_signOu = root.findViewById<Button>(R.id.btn_signOut)
        val textView = root.findViewById<TextView>(R.id.textView4)
        val firebaseUser = mAuth.currentUser
        if (firebaseUser == null) {
            val mintent: Intent? = Intent(context, LoginActivity::class.java)
            startActivityForResult(mintent, 1)
        } else {
            Log.d("Log", "from account page ${firebaseUser.email.toString()}")
            textView.text = "добрый день,\n ${firebaseUser.email}"


        }
        btn_signOu.setOnClickListener {
            mAuth.signOut()

            activity?.recreate()
        }
        return root

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            activity?.recreate()
        }


    }
}
