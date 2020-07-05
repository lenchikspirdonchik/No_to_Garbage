package spiridonov.no_to_garbage.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_account.*
import spiridonov.no_to_garbage.LoginActivity
import spiridonov.no_to_garbage.R


class AccountFragment : Fragment() {
    var isSiqnIn = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (!isSiqnIn) {
            var mintent: Intent? = Intent(context, LoginActivity::class.java)
            startActivityForResult(mintent, 1)

        }





        return inflater.inflate(R.layout.fragment_account, container, false)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {
                val email: String = data!!.getStringExtra("key_email")
                textView4.text = email
            }
        }

    }
}