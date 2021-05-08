package com.dayo.executer

import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.content.Intent
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dayo.executer.data.DataManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    var vifo = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataManager.loadSettings()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val mapButton: FloatingActionButton = findViewById(R.id.mapButton)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_weekly, R.id.navigation_lost_thing, R.id.navigation_setting, R.id.navigation_legacy))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        CoroutineScope(Dispatchers.Default).launch {
            val doc = Jsoup.connect("http://34.70.245.122/version.html").get()
            vifo = doc.body().text() //ablr asck ex
        }

        mapButton.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            startActivity(mapIntent)
        }

        Toast.makeText(this, "버전 정보를 불러오고 있습니다.", Toast.LENGTH_SHORT).show()

    }
}