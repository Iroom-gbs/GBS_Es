package com.dayo.executer.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.util.Log
import androidx.core.content.edit
import com.dayo.executer.App
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.util.*

class DataManager {
    companion object {
        var weeklyTimeTableData = mutableListOf<MutableList<TimeTableData>>() // 주간 시간표 리스트
        var timeTableData = mutableListOf<TimeTableData>() // 일일 시간표 리스트
        var todayAblrTableData = mutableListOf<AblrData>() // 금일 학습실 신청 리스트
        var tmpAblrData = mutableListOf<AblrData>() // 삭제 예정
        var mealData = mutableListOf<MutableList<MealData>>() // 금일 급식 데이터
        var lostAndFoundData = mutableListOf<LostAndFoundInfo>()

        private val sharedPref = App.appContext!!.getSharedPreferences("settings", MODE_PRIVATE)
        var ablrID = "" // 스마트 기숙관리 ID
        var ablrPW = "" // 스마트 기숙관리 비밀번호
        var asckPW = "" // 자가진단 비밀번호
        var asckName = ""
        var asckBirth = ""
        var classInfo = "" // 학년, 반 번호

        var asckUseAdvOpt = false // (구) 자가진단 관련 고급 옵션 표시, (변경) 고급 옵션 표시
        var alwaysReceiveAsckAlert = false // 아침마다 자가진단 데이터 수신 여부

        var alwaysReceiveTimeTableData = false // 매 시간마다 시간표 수신 여부
        var receiveSwdTimeTableData = false // 변경된 시간마다 시간표 수신 여부

        var lowProtect = false // 예측하지 못한 http 통신 허용 여부

        var dayOfWeek = -1 // 말그대로 일주일중 몇번째날인가

        var online = false // 인터넷 연결 여부

        var vifo = "" // 최신 버전 정보

        var receiveDebugFCMData = false // FCM 디버깅 데이터 수신 여부

        fun saveSettings() {
            sharedPref.edit {
                putString("ablr$dayOfWeek", AblrData.ablrDataToString(todayAblrTableData))
                Log.d("asdf", "$dayOfWeek ${AblrData.ablrDataToString(todayAblrTableData)}")
                putString("ablrID", ablrID)
                putString("ablrPW", ablrPW)
                putString("asckPW", asckPW)
                putString("asckName", asckName)
                putString("asckBirth", asckBirth)
                putString("classInfo", classInfo)
                putBoolean("asckUseAdvOpt", asckUseAdvOpt)
                putBoolean("alwaysReceiveAsckAlert", alwaysReceiveAsckAlert)
                putBoolean("lowProtect", lowProtect)
                putBoolean("alwaysReceiveTimeTableData", alwaysReceiveTimeTableData)
                putBoolean("receiveSwdTimeTableData", receiveSwdTimeTableData)
                putBoolean("receiveDebugFCMData", receiveDebugFCMData)
                apply()
            }
        }

        /**
         * @suppress Hello World
         * @return 네트워크 정보 수신 성공시 true 반환, 실패시 false 반환, 서버가 닫힌경우엔 true를 반환합니다.
         */
        fun loadSettings(): Boolean {
            CoroutineScope(Dispatchers.IO).launch {
                //Clear list
                todayAblrTableData = mutableListOf()
                mealData = mutableListOf()

                dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                Log.d("asdf", dayOfWeek.toString())

                //Initialize data from sharedPreference
                val ablrData = sharedPref.getString("ablr$dayOfWeek", "")!!
                for (i in AblrData.stringToAblrData(ablrData))
                    todayAblrTableData.add(i)
                ablrID = sharedPref.getString("ablrID", "")!!
                ablrPW = sharedPref.getString("ablrPW", "")!!
                asckName = sharedPref.getString("asckName", "")!!
                asckBirth = sharedPref.getString("asckBirth", "")!!
                asckPW = sharedPref.getString("asckPW", "")!!
                classInfo = sharedPref.getString("classInfo", "1-1")!!
                asckUseAdvOpt = sharedPref.getBoolean("asckUseAdvOpt", false)
                alwaysReceiveAsckAlert = sharedPref.getBoolean("alwaysReceiveAsckAlert", false)
                lowProtect = sharedPref.getBoolean("lowProtect", false)
                alwaysReceiveTimeTableData =
                    sharedPref.getBoolean("alwaysReceiveTimeTableData", false)
                receiveSwdTimeTableData = sharedPref.getBoolean("receiveSwdTimeTableData", false)
                receiveDebugFCMData = sharedPref.getBoolean("receiveDebugFCMData", false)
                tmpAblrData = mutableListOf()
                tmpAblrData.addAll(todayAblrTableData)
            }
            return loadNetworkData()
        }

        private fun loadNetworkData(): Boolean {
            //Check is user online
            if(!online) return false
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    //Get least version info
                    vifo = HttpConnection.DownloadString("http://20.41.76.129/gbses/version")
                    //Get Timetable info
                    var tableData = HttpConnection.DownloadString("http://20.41.76.129/api/timetable/${classInfo[0]}/${classInfo[2]}")
                    tableData = tableData.replace("null", "")
                    Log.d("asdf", tableData)
                    if (tableData == "not parsed yet") {
                        timeTableData.add(TimeTableData("서버 오류!", "", "", "", "", ""))
                    } else {
                        weeklyTimeTableData = TimeTableData.stringToTimeTableData(tableData)
                        timeTableData = weeklyTimeTableData[dayOfWeek - 1]
                        if (timeTableData.size == 0)
                            timeTableData.add(TimeTableData("정규수업이 없습니다!", "", "", "", "", ""))
                    }
                    //Get meal info
                    val mdt = HttpConnection.DownloadString("http://20.41.76.129/api/meal")
                    var idx = 0
                    if (mdt == "Not parsed yet" || mdt == "") {
                        mealData.add(mutableListOf(MealData("서버 오류!", MealData.allFalseList)))
                    } else if (mdt == "*| *| *| ") {
                        mealData.add(mutableListOf(MealData("급식 정보가 없습니다.", MealData.allFalseList)))
                    } else {
                        mealData.add(mutableListOf())
                        for (x in mdt.split(' ')) {
                            if (x == "석식") break
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
                    var dString = HttpConnection.DownloadString("http://20.41.76.129/lostandfound")
                    dString = HttpConnection.PreParseJson(dString)
                    val json = Gson().fromJson(dString, Array<LostAndFoundInfo>::class.java)
                    json.forEach { lostAndFoundData.add(it) }
                }
                catch(_: Exception){
                    //When server closed like Gateway error(502) or not found(404)
                    timeTableData.add(TimeTableData("서버 오류!", "", "", "", "", ""))
                    vifo = "2.1.1"
                    mealData.add(mutableListOf(MealData("서버 오류!", MealData.allFalseList)))
                }
            }
            return true
        }

        init{
            //유저의 인터넷 연결 상테 변경시 발생하는 콜백 함수 생성
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
