package com.dayo.executer.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.content.ContextCompat.startForegroundService
import com.dayo.executer.*
import com.dayo.executer.data.AblrData
import com.dayo.executer.data.DataManager
import com.dayo.executer.data.MealData
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
        val mealTable = view?.findViewById<TableLayout>(R.id.mealTable)

        timeTable?.removeAllViews()

        sv?.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (oldScrollY - 5 > scrollY)
                nav.visibility = View.GONE
            else if (oldScrollY + 5 < scrollY)
                nav.visibility = View.VISIBLE
        }

        //Wait for INIT vifo
        while (m.vifo == "")
            Thread.sleep(1)

        if (m.packageManager.getPackageInfo("com.dayo.executer", PackageManager.GET_ACTIVITIES).versionName != m.vifo.split(' ')[2])
            Toast.makeText(activity, "업데이트가 필요합니다.", Toast.LENGTH_LONG).show()

        asckBtn?.setOnClickListener {
            startActivity(Intent(m, AsckActivity::class.java))
        }

        timeTable?.removeAllViews()
        for(i in DataManager.timeTableData)
            timeTable?.addView(TimeTableRow(m, i))

        applyAblrBtn?.setOnClickListener {
            val intent = Intent(activity, AblrService::class.java)
            if(DataManager.noTempDataInHomeFragment)
                intent.putExtra("ablr", AblrData.ablrDataToString(DataManager.todayAblrTableData))
            else intent.putExtra("ablr", AblrData.ablrDataToString(DataManager.tmpAblrData))
            startForegroundService(activity as MainActivity, intent)
        }

        mealTable?.removeAllViews()
        for(i in DataManager.mealData){
            for(j in i){
                mealTable?.addView(MealTableRow(activity as MainActivity, j))
            }
            mealTable?.addView(BlankTableRow(activity as MainActivity))
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
                val row = AblrTableRow(m, DataManager.todayAblrTableData[i])
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
            val row = AblrTableRow(m, DataManager.tmpAblrData[i])
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

    class TextRow(context: Context, text: String): TableRow(context) {
        val space = TextView(context)
        private fun addView(){
            super.removeAllViews()
            super.addView(space)
        }
        init{
            space.text = text
            addView()
        }
    }

    fun BlankTableRow(context: Context): TableRow{
        return TextRow(context, " ")
    }

    class MealTableRow(context: Context, mealData: MealData): TableRow(context) {
        var mealInfo: TextView = TextView(context)

        private fun addView(){
            super.removeAllViews()
            super.addView(mealInfo)
        }
        init{
            mealInfo.text = mealData.menu
            addView()
        }
    }

    class AblrTableRow(context: Context, ablrData: AblrData): TableRow(context) {
        var timeInfo: TextView = TextView(context)
        var subjectInfo: TextView = TextView(context)
        var editBtn: Button = Button(context)
        var removeBtn: Button = Button(context)

        private fun addView() {
            super.removeAllViews()
            super.addView(timeInfo)
            super.addView(subjectInfo)
            super.addView(editBtn)
            super.addView(removeBtn)
        }

        init {
            editBtn.text = "EDIT"
            removeBtn.text = "REMOVE"
            timeInfo.text = ablrData.getFullTime()
            subjectInfo.text = resources.getStringArray(R.array.place_list)[resources.getStringArray(R.array.place_data_list).indexOf(ablrData.locationInfo)]
            addView()
        }
    }

    class TimeTableRow(context: Context, timeTableData: TimeTableData): TableRow(context) {
        var timeIndex: TextView = TextView(context)
        var timeInfo: TextView = TextView(context)
        var subjectInfo: TextView = TextView(context)
        var tInfo: TextView = TextView(context)
        var elseInfo: TextView = TextView(context)
        var roomInfo: TextView = TextView(context)

        private fun addView() {
            super.removeAllViews()
            super.addView(timeIndex)
            super.addView(timeInfo)
            super.addView(subjectInfo)
            super.addView(tInfo)
            super.addView(roomInfo)
            super.addView(elseInfo)
        }

        init {
            timeIndex.text = timeTableData.timeidx
            timeInfo.text = timeTableData.timeInfo
            subjectInfo.text = timeTableData.subjectInfo
            tInfo.text = timeTableData.teacherInfo
            elseInfo.text = timeTableData.elseInfo
            roomInfo.text = timeTableData.roomInfo
            addView()
        }
    }
}
