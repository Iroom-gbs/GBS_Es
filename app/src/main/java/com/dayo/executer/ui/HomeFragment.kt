package com.dayo.executer.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import com.dayo.executer.*
import com.dayo.executer.data.AblrData
import com.dayo.executer.data.DataManager
import com.dayo.executer.data.TimeTableData
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {

    lateinit var m: MainActivity
    lateinit var nav: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun initUI() {
        m = (activity as MainActivity)
        nav = m.findViewById(R.id.nav_view)

        val sv = view?.findViewById<ScrollView>(R.id.scv)
        val timeTable = view?.findViewById<TableLayout>(R.id.timeTable)
        val asckBtn = view?.findViewById<Button>(R.id.sckBtn)
        val applyAblrBtn = view?.findViewById<Button>(R.id.applyAblrBtn)
        val mealTableLinearLayout = view?.findViewById<LinearLayout>(R.id.hscvl)

        timeTable?.removeAllViews()

        sv?.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (oldScrollY - 5 > scrollY)
                nav.visibility = View.GONE
            else if (oldScrollY + 5 < scrollY)
                nav.visibility = View.VISIBLE
        }

        //Wait for INIT vifo
        while (DataManager.vifo == "")
            Thread.sleep(1)

        if (m.packageManager.getPackageInfo("com.dayo.executer", PackageManager.GET_ACTIVITIES).versionName != DataManager.vifo)
            Toast.makeText(activity, "업데이트가 필요합니다.", Toast.LENGTH_LONG).show()

        asckBtn?.setOnClickListener {
            startActivity(Intent(m, AsckActivity::class.java))
        }

        timeTable?.removeAllViews()
        for(i in DataManager.timeTableData)
            timeTable?.addView(TimeTableData.TimeTableRow(m, i))

        applyAblrBtn?.setOnClickListener {
            val intent = Intent(activity, AblrService::class.java)
            if(DataManager.noTempDataInHomeFragment)
                intent.putExtra("ablr", AblrData.ablrDataToString(DataManager.todayAblrTableData))
            else intent.putExtra("ablr", AblrData.ablrDataToString(DataManager.tmpAblrData))
            startForegroundService(activity as MainActivity, intent)
        }

        mealTableLinearLayout?.removeAllViews()
        for(i in DataManager.mealData){
            val mealTextView = TextView(this.activity)
            var mealString = ""
            for(j in i){
                mealString += j.menu + "\n"
            }
            Log.d("meal",mealString)
            mealTextView.text = mealString
            mealTextView.gravity = Gravity.CENTER
            mealTextView.setPadding(30,0,0,30)
            mealTableLinearLayout?.addView(mealTextView)
        }

        initAblrTable()

        m.findViewById<FloatingActionButton>(R.id.addAblrDataFab)!!.setOnClickListener {
            val intent = Intent(activity, EditAblrActivity::class.java)
            if(!DataManager.noTempDataInHomeFragment)
                intent.putExtra("dataInfo", "tmp")
            startActivity(intent)
        }
    }

    fun initAblrTable() {
        val ablrTable = view?.findViewById<TableLayout>(R.id.ablrTable)
        ablrTable?.removeAllViews()
        if(DataManager.noTempDataInHomeFragment)
            for(i in DataManager.todayAblrTableData.indices) {
                val row = AblrData.AblrTableRow(m, DataManager.todayAblrTableData[i])
                row.editBtn.setOnClickListener {
                    startActivity(Intent(activity, EditAblrActivity::class.java).putExtra("edt", i))
                }
                row.removeBtn.setOnClickListener {
                    DataManager.todayAblrTableData.removeAt(i)
                    DataManager.saveSettings()
                    initAblrTable()
                }
                ablrTable?.addView(row)
            }
        else for(i in DataManager.tmpAblrData.indices) {
            val row = AblrData.AblrTableRow(m, DataManager.tmpAblrData[i])
            row.editBtn.setOnClickListener {
                startActivity(Intent(activity, EditAblrActivity::class.java).putExtra("edt", i).putExtra("dataInfo", "tmp"))
            }
            row.removeBtn.setOnClickListener {
                DataManager.tmpAblrData.removeAt(i)
                DataManager.saveSettings()
                initAblrTable()
            }
            ablrTable?.addView(row)
        }
    }

    override fun onStart() {
        super.onStart()

        initUI()
    }
}
