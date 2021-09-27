package com.dayo.executer.data

import android.content.Context
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import com.dayo.executer.R

/**
 * @param sth 시작시간 (hour)
 * @param stm 시작시간의 (min)
 * @param eth 종료시간의 (hour)
 * @param etm 종료시간의 (min)
 * @param locationInfo 진행 장소
 * @constructor a
 */
data class AblrData(var sth: String, var stm: String, var eth: String, var etm: String, var locationInfo: String) {

    constructor() : this("", "", "", "", "") {

    }

    /**
     * @return 시간을 sth:stm ~ eth:etm의 형식으로 구합니다.
     */
    fun getFullTime(): String = "$sth:$stm ~ $eth:$etm"

    companion object {
        /**
         * @param s AblrData의 커스텀 포멧 형식의 문자열
         * @return 커스텀 포멧을 AblrData로 변환한 결과
         */
        fun stringToAblrData(s: String): MutableList<AblrData> {
            val rtn = mutableListOf<AblrData>()
            val psdat = s.split(' ')
            try {
                for (i in psdat.indices step (5)) {
                    rtn.add(
                        AblrData(
                            sth = psdat[i].replace('_', ' '),
                            stm = psdat[i + 1].replace('_', ' '),
                            eth = psdat[i + 2].replace('_', ' '),
                            etm = psdat[i + 3].replace('_', ' '),
                            locationInfo = psdat[i + 4].replace('_', ' ')
                        )
                    )
                }
            }
            catch(e: IndexOutOfBoundsException){

            }
            return rtn
        }

        /**
         * @param dat 문자열로 변환할 AblrData의 배열
         * @return 문자열로 변환된 AblrData
         */
        fun ablrDataToString(dat: List<AblrData>): String {
            var rtn = ""
            for(i in dat) {
                rtn += i.toString()
            }
            rtn.dropLast(1)
            return rtn
        }
    }

    /**
     * @return 하나의 AblrData를 문자열로 변환한 값
     */
    override fun toString(): String {
        return "$sth $stm $eth $etm $locationInfo "
    }

    /**
     * @param context Context
     * @param ablrData Row로 변환할 Ablrdata
     */
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
            subjectInfo.text = resources.getStringArray(R.array.place_list)[resources.getStringArray(
                R.array.place_data_list).indexOf(ablrData.locationInfo)]
            addView()
        }
    }
}