package com.dayo.executer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.app.AlertDialog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dayo.executer.data.DataManager
import com.dayo.executer.ui.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging

//TODO: Convert AppCompatActivity to Activity
class MainActivity : AppCompatActivity() {
    private val fragmenthome: Fragment = HomeFragment()
    private val fragmentweelky:Fragment = WeeklyFragment()
    private val fragmentlostthing: Fragment = LostThingInfoFragment()
    private val fragmentsetting: Fragment = SettingsFragment()
    private val fragmentmap: Fragment = MapFragment()
    private var active : Fragment = fragmenthome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if(!task.isSuccessful){
                Toast.makeText(baseContext, "FCM token generation failed.", Toast.LENGTH_LONG).show()
                return@OnCompleteListener
            }

            val token = task.result
            Log.d("asdf", token.toString())
            //Toast.makeText(baseContext, token, Toast.LENGTH_LONG).show()
        })

        if(!DataManager.loadSettings()){
            Toast.makeText(this, "인터넷이 연결되어있지 않아 앱을 종료합니다.", Toast.LENGTH_LONG).show()
            finishAndRemoveTask()
            //TODO: Why this alert dialog creation is not working
                /*
            AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("이 앱을 사용하기 위해선 인터넷 연결이 필요합니다.")
                //.setCancelable(false)
                .setPositiveButton("OK"){ _, _ ->
                    finishAndRemoveTask()
                }
                .show()
                 */
        }
        else {
            val navView: BottomNavigationView = findViewById(R.id.nav_view)

            val navController = findNavController(R.id.nav_host_fragment)

            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_weekly,
                    R.id.navigation_lost_thing,
                    R.id.navigation_setting,
                    R.id.navigation_map
                )
            )

            //setupActionBarWithNavController(navController, appBarConfiguration)
            //navView.setOnNavigationItemSelectedListener(mnavviewitemselectedListener)
            //navView.setOnNavigationItemReselectedListener(mnavviewitemreselectedListener)
            navView.setupWithNavController(navController)

            Toast.makeText(this, "버전 정보를 불러오고 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private val mnavviewitemreselectedListener = BottomNavigationView.OnNavigationItemReselectedListener { item->
        when(item.itemId) {
            R.id.navigation_map -> {
                var drawer: SlidingUpPanelLayout = findViewById(R.id.main_panel)
                if(drawer.panelState != SlidingUpPanelLayout.PanelState.DRAGGING)
                drawer.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED)
                true
            }
        }
        false
    }

    private val mnavviewitemselectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when(item.itemId) {
            R.id.navigation_home -> {
                findViewById<FloatingActionButton>(R.id.addAblrDataFab).visibility = View.VISIBLE
                var navView: BottomNavigationView = findViewById(R.id.nav_view)
                var menunav: Menu = navView.menu
                var mapitem: MenuItem = menunav.findItem(R.id.navigation_map)

                mapitem.setIcon(R.drawable.ic_baseline_map_24)
                mapitem.title = "지도"

                changeFragment(fragmenthome)
                true
            }
            R.id.navigation_weekly -> {
                findViewById<FloatingActionButton>(R.id.addAblrDataFab).visibility = View.GONE
                var navView: BottomNavigationView = findViewById(R.id.nav_view)
                var menunav: Menu = navView.menu
                var mapitem: MenuItem = menunav.findItem(R.id.navigation_map)

                mapitem.setIcon(R.drawable.ic_baseline_map_24)
                mapitem.title = "지도"

                changeFragment(fragmentweelky)
                true
            }
            R.id.navigation_lost_thing -> {
                findViewById<FloatingActionButton>(R.id.addAblrDataFab).visibility = View.GONE
                var navView: BottomNavigationView = findViewById(R.id.nav_view)
                var menunav: Menu = navView.menu
                var mapitem: MenuItem = menunav.findItem(R.id.navigation_map)

                mapitem.setIcon(R.drawable.ic_baseline_map_24)
                mapitem.title = "지도"

                changeFragment(fragmentlostthing)
                true
            }
            R.id.navigation_setting -> {
                findViewById<FloatingActionButton>(R.id.addAblrDataFab).visibility = View.GONE
                var navView: BottomNavigationView = findViewById(R.id.nav_view)
                var menunav: Menu = navView.menu
                var mapitem: MenuItem = menunav.findItem(R.id.navigation_map)

                mapitem.setIcon(R.drawable.ic_baseline_map_24)
                mapitem.title = "지도"

                changeFragment(fragmentsetting)
                true
            }
            R.id.navigation_map -> {
                findViewById<FloatingActionButton>(R.id.addAblrDataFab).visibility = View.GONE
                var navView: BottomNavigationView = findViewById(R.id.nav_view)
                var menunav: Menu = navView.menu
                var mapitem: MenuItem = menunav.findItem(R.id.navigation_map)

                mapitem.setIcon(R.drawable.ic_baseline_search_24)
                mapitem.title = "검색"

                changeFragment(fragmentmap)
                true
            }
            else -> false
        }
    }

    private fun changeFragment(fragment: Fragment) {
        if(fragment!=active) {
            val ft: FragmentTransaction= supportFragmentManager.beginTransaction()
            ft.replace(R.id.nav_host_fragment, fragment)
            //ft.detach(active)
            ft.commit()
            active = fragment
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("종료하시겠습니까?")
            .setTitle("종료")
            .setPositiveButton("OK"){ _, _ -> finishAndRemoveTask()}
            .setNegativeButton("NO"){ _, _ -> }
            .create().show()
    }
}