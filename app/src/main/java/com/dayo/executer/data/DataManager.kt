package com.dayo.executer.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.edit
import com.dayo.executer.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.util.*


class DataManager {
    companion object {
        var weeklyTimeTableData = mutableListOf<MutableList<TimeTableData>>()
        var timeTableData = mutableListOf<TimeTableData>()
        var todayAblrTableData = mutableListOf<AblrData>()
        var tmpAblrData = mutableListOf<AblrData>()
        var mealData = mutableListOf<MutableList<MealData>>()

        private val sharedPref = App.appContext!!.getSharedPreferences("settings", MODE_PRIVATE)
        var ablrID = ""
        var ablrPW = ""
        var asckPW = ""
        var classInfo = ""
        var noTempDataInHomeFragment = false

        var asckDt = 0L
        var asckDsel = 0L
        var asckDs = 0L
        var asckUseAdvOpt = false

        var lowProtect = false

        var dayOfWeek = -1

        var online = false

        var vifo = ""

        fun saveSettings() {
            sharedPref.edit {
                putString("ablr$dayOfWeek", AblrData.ablrDataToString(todayAblrTableData))
                Log.d("asdf", "$dayOfWeek ${AblrData.ablrDataToString(todayAblrTableData)}")
                putString("ablrID", ablrID)
                putString("ablrPW", ablrPW)
                putString("asckPW", asckPW)
                putString("classInfo", classInfo)
                putLong("asckDt", asckDt)
                putLong("asckDsel", asckDsel)
                putLong("asckDs", asckDs)
                putBoolean("asckUseAdvOpt", asckUseAdvOpt)
                putBoolean("lowProtect", lowProtect)
                putBoolean("noTempDataInHomeFragment", noTempDataInHomeFragment)
                apply()
            }
        }

        fun loadSettings(): Boolean {
            todayAblrTableData = mutableListOf()
            mealData = mutableListOf()

            dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            Log.d("asdf", dayOfWeek.toString())
            var ablrData = sharedPref.getString("ablr$dayOfWeek", "")!!
            for (i in AblrData.stringToAblrData(ablrData))
                todayAblrTableData.add(i)
            ablrID = sharedPref.getString("ablrID", "")!!
            ablrPW = sharedPref.getString("ablrPW", "")!!
            asckPW = sharedPref.getString("asckPW", "")!!
            classInfo = sharedPref.getString("classInfo", "1-1")!!
            asckDt = sharedPref.getLong("asckDt", 10L)
            asckDsel = sharedPref.getLong("asckDsel", 1500L)
            asckDs = sharedPref.getLong("asckDs", 1000L)
            asckUseAdvOpt = sharedPref.getBoolean("asckUseAdvOpt", false)
            lowProtect = sharedPref.getBoolean("lowProtect", false)
            noTempDataInHomeFragment = sharedPref.getBoolean("noTempDataInHomeFragment", false)
            tmpAblrData = mutableListOf()
            tmpAblrData.addAll(todayAblrTableData)
            return loadNetworkData()
        }

        private fun loadNetworkData(): Boolean {
            if(!online) return false
            CoroutineScope(Dispatchers.Default).launch {
                val doc = Jsoup.connect("http://20.41.76.129/gbses/version")
                    .ignoreContentType(true).get()
                vifo = doc.body().text() //ablr asck ex
                vifo = vifo.substring(1, vifo.length - 1)
            }
            var tableData = ""
            CoroutineScope(Dispatchers.Default).launch {
                val doc =
                    Jsoup.connect("http://20.41.76.129/api/timetable/${classInfo[0]}/${classInfo[2]}")
                        .ignoreContentType(true).get()
                tableData = doc.body().text()
            }
            while (tableData == "")
                Thread.sleep(100)
            tableData = tableData.substring(1, tableData.length - 1)
            Log.d("asdf", tableData)
            if(tableData == "not parsed yet"){
                timeTableData.add(TimeTableData("서버 오류!", "", "", "", "", ""))
            }
            else {
                weeklyTimeTableData = TimeTableData.stringToTimeTableData(tableData)
                timeTableData = weeklyTimeTableData[dayOfWeek - 1]
                if (timeTableData.size == 0)
                    timeTableData.add(TimeTableData("정규수업이 없습니다!", "", "", "", "", ""))
            }
            var mdt = ""
            CoroutineScope(Dispatchers.Default).launch {
                val doc = Jsoup.connect("http://20.41.76.129/api/meal/")
                    .ignoreContentType(true).get()
                mdt = doc.body().text()
                Log.d("asdf", mdt)
            }
            while(mdt == "")
                Thread.sleep(100)
            mdt = mdt.substring(1, mdt.length - 1)
            var idx = 0
            if(mdt == "Not parsed yet"){
                mealData.add(mutableListOf(MealData("서버 오류!", MealData.allFalseList)))
            }
            else if(mdt == "*| *| *| "){
                mealData.add(mutableListOf(MealData("급식 정보가 없습니다.", MealData.allFalseList)))
            }
            else {
                mealData.add(mutableListOf())
                for (x in mdt.split(' ')) {
                    if (x == "*|") {
                        mealData.add(mutableListOf())
                        Log.d("asdf", mealData[idx].joinToString())
                        idx++
                        if (idx == 3) break
                    } else {
                        mealData[idx].add(MealData.stringToMealData(x))
                    }
                }
            }
            return true
        }
        init{
            try {
                (App.appContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                    .registerDefaultNetworkCallback(object : NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            online = true
                        }

                        override fun onLost(network: Network) {
                            online = false
                        }
                    })
                online = false
            } catch (e: Exception) {
                online = false
            }
        }
    }
}
