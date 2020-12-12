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
import com.yandex.mapkit.mapview.MapView


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mAuth: FirebaseAuth
    private var mapview: MapView? = null
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
                setOf(R.id.nav_home, R.id.nav_account, R.id.nav_addMap, R.id.nav_mapTest),
                drawerLayout
            )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth.currentUser
        val header = findViewById<TextView>(R.id.header_name)
        /*  if (firebaseUser != null && header != null) {
              header.text = firebaseUser.email
          }*/
        val navigationView =
            findViewById<NavigationView>(R.id.main_mobile_navigation) as NavigationView?
        if (navigationView != null) {
            val mHeaderView: View = navigationView.getHeaderView(0)
            mHeaderView.findViewById<TextView>(R.id.header_name).text = "Test Company Name, LLC"
        }
        mapview = findViewById<MapView>(R.id.mapview)

        MapKitFactory.setApiKey("fd59b9d8-89f7-4bc6-aac0-48391066dd80")
        MapKitFactory.initialize(this)

    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()

        if (mapview != null) {
            mapview!!.onStop()
            MapKitFactory.getInstance().onStop()
        }

    }

    override fun onStart() {
        super.onStart()
        if (mapview != null) {
            mapview!!.onStart()
            MapKitFactory.getInstance().onStart()
        }
    }

}