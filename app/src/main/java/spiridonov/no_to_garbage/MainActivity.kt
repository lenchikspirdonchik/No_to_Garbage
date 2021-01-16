package spiridonov.no_to_garbage


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.yandex.mapkit.MapKitFactory


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.main_nav_view)
        val navController = findNavController(R.id.main_nav_host_fragment)
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_account,
                    R.id.nav_addMap,
                    R.id.nav_delete,
                    R.id.nav_aboutApp
                ),
                drawerLayout
            )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mAuth = FirebaseAuth.getInstance()

        val navigationView =
            findViewById<NavigationView>(R.id.main_mobile_navigation) as NavigationView?
        if (navigationView != null) {
            val mHeaderView: View = navigationView.getHeaderView(0)
            mHeaderView.findViewById<TextView>(R.id.header_name).text = "Test Company Name, LLC"
        }

        MapKitFactory.setApiKey("fd59b9d8-89f7-4bc6-aac0-48391066dd80")
        MapKitFactory.initialize(this)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}