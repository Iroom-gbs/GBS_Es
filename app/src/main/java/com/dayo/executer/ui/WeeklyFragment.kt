package com.dayo.executer.ui

import TextRow
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.res.ResourcesCompat
import com.dayo.executer.R
import com.dayo.executer.data.DataManager
import com.dayo.executer.data.TimeTableData
import org.w3c.dom.Text
import java.lang.IndexOutOfBoundsException

class WeeklyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly, container, false)
    }

    override fun onStart(){
        super.onStart()

        val weeklyTimeTable = view?.findViewById<TableLayout>(R.id.weeklyTimeTable)
        weeklyTimeTable?.removeAllViews()
        val rowx = TableRow(activity)
        rowx.addView(TextRow.BlankTableRow(requireContext()))
        for(x in "월화수목금") {
            val k = TextRow(requireContext(), x.toString())
            k.textView.gravity = Gravity.CENTER
            k.background = ResourcesCompat.getDrawable(resources, R.drawable.border, resources.newTheme())
            rowx.addView(k)
        }
        weeklyTimeTable?.addView(rowx)
        for(i in 0..8){
            val row = TableRow(activity)
            for(j in 0..5) {
                try {
                    row.addView(
                        TimeTableData.SimpleTimeTableRow(
                            requireActivity().baseContext,
                            DataManager.weeklyTimeTableData[j][i]
                        )
                    )
                }
                catch(ei: IndexOutOfBoundsException){
                    Log.d("asdf", "$j $i")
                    row.addView(TextRow.BlankTableRow(requireContext()))
                }
            }
            weeklyTimeTable?.addView(row)
        }
    }
}