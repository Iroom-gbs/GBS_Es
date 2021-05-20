package com.dayo.executer.data

import android.util.Log

data class TimeTableData(val timeidx: String, val timeInfo: String, val subjectInfo: String, val teacherInfo: String, val roomInfo: String, val elseInfo: String) {
    companion object {
        fun stringToTimeTableData(s: String): MutableList<MutableList<TimeTableData>> {
            Log.d("asdf", s)
            val rtn = mutableListOf(mutableListOf<TimeTableData>())
            rtn.add(mutableListOf())
            val psdat = s
            var idx = 1
            for(i in psdat.split('`')){
                rtn.add(mutableListOf())
                if(i.length < 2){
                    idx++
                    continue
                }
                for(time in i.split('^')) {
                    if (time.length < 2) break

                    val dat = time.split('|')
                    if (dat.size < 5) break
                    rtn[idx].add(
                        TimeTableData(
                            timeidx = dat[0],
                            timeInfo = dat[1],
                            subjectInfo = dat[2],
                            teacherInfo = dat[3],
                            roomInfo = dat[4],
                            elseInfo = dat[5]
                        )
                    )
                }
                idx++
            }
            return rtn
        }
    }
}

/*
 * Example data
 *
 * timeInfo: time
 * subjectInfo: subject
 * teacherInfo: name of teacher
 * roomInfo: location
 * elseInfo: other info(exam, homework, etc.)
 *
 * 9:10~10:00 영어 서원화 어학실2 암것도_없음
 */