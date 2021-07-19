package com.dayo.executer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dayo.executer.data.DataManager
import com.dayo.executer.ui.*
import com.dayo.executer.ui.home.InfoViewPageAdapter
import com.dayo.executer.ui.menu.MenuDialog
import com.dayo.executer.ui.menu.MenuDialogOnClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton

//TODO: Convert AppCompatActivity to Activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (packageManager.getPackageInfo("com.dayo.executer", PackageManager.GET_ACTIVITIES).versionName != DataManager.vifo)
            Toast.makeText(this, "업데이트가 필요합니다.", Toast.LENGTH_LONG).show()

        val infoViewPage = findViewById<ViewPager2>(R.id.InfoViewPage)
        infoViewPage.adapter = InfoViewPageAdapter(this)

        findViewById<FloatingActionButton>(R.id.menu_fab).setOnClickListener {
            MenuDialog(this, object: MenuDialogOnClickListener {
                override fun OnAsckBtnClick() {
                    startActivity(Intent(this@MainActivity, AsckActivity::class.java))
                }

                override fun OnMapBtnClick() {
                    Toast.makeText(this@MainActivity, "Not supported yet", Toast.LENGTH_LONG).show()
                }

                override fun OnAblrBtnClick() {
                    Toast.makeText(this@MainActivity, "Not supported yet", Toast.LENGTH_LONG).show()
                }

            }).show()
        }

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}