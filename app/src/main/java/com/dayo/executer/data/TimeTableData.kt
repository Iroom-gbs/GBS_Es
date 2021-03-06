package com.dayo.executer.data

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.dayo.executer.R

class NewHomeTimeTableRow(context: Context, timeTableData: TimeTableData): TableRow(context){
    init {
        val timeTextView = TextView(context)
        timeTextView.text = timeTableData.timeidx
        super.addView(timeTextView)
        val subjectTextView = TextView(context)
        subjectTextView.text = timeTableData.subjectInfo
        super.addView(subjectTextView)
        val teacherInfo = TextView(context)
        teacherInfo.text = timeTableData.teacherInfo
        super.addView(teacherInfo)
    }
}

data class TimeTableData(val timeidx: String, val timeInfo: String, val subjectInfo: String, val teacherInfo: String, val roomInfo: String, val elseInfo: String, val changed: Boolean = false) {
    companion object {
        fun stringToTimeTableData(s: String): MutableList<MutableList<TimeTableData>> {
            Log.d("asdf", s)
            val rtn = mutableListOf(mutableListOf<TimeTableData>())
            rtn.add(mutableListOf())
            var idx = 1
            for (i in s.split('`')) {
                rtn.add(mutableListOf())
                if (i.length < 2) {
                    idx++
                    continue
                }
                for (time in i.split('^')) {
                    if (time.length < 2) break

                    val dat = time.split('|')
                    if (dat.size < 6) break
                    rtn[idx].add(
                        TimeTableData(
                            timeidx = dat[0],
                            timeInfo = dat[1],
                            subjectInfo = dat[2],
                            teacherInfo = dat[3],
                            roomInfo = dat[4],
                            elseInfo = dat[5],
                            changed = when (dat[6]) {
                                "1" -> true
                                else -> false
                            }
                        )
                    )
                }
                idx++
            }
            return rtn
        }
    }

    class SimpleTimeTableRow(context: Context, private val timeTableData: TimeTableData): TableRow(context) {

        private fun addView() {
            super.setBackground(ResourcesCompat.getDrawable(resources, R.drawable.border, resources.newTheme()))
            super.setGravity(Gravity.CENTER)
            super.setGravity(Gravity.CENTER)
            val tx = TextView(context)
            tx.gravity = Gravity.CENTER
            tx.textSize = 12f
            if(timeTableData.changed)
                tx.setBackgroundColor(0xFFFF00)
            tx.text = "\n${timeTableData.subjectInfo}\n${timeTableData.teacherInfo}\n${timeTableData.roomInfo}\n"
            super.addView(tx)
        }

        init {
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

/*
 * Example data
 *
 * timeInfo: time
 * subjectInfo: subject
 * teacherInfo: name of teacher
 * roomInfo: location
 * elseInfo: other info(exam, homework, etc.)
 * changed: true in timetable changed in that time
 */