package com.dayo.executer.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import com.dayo.executer.R
import com.dayo.executer.data.DataManager
import com.dayo.executer.data.TimeTableData
import java.lang.IndexOutOfBoundsException

class WeeklyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly, container, false)
    }

    fun initUI(){
        val weeklyTimeTable = view?.findViewById<TableLayout>(R.id.weeklyTimeTable)
        weeklyTimeTable?.removeAllViews()
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
                    Log.d("asdf", "$i $j")
                    Log.d("asdf",
                        DataManager.weeklyTimeTableData[j][i].toString())
                }
                catch(ei: IndexOutOfBoundsException){
                    row.addView(TextRow.BlankTableRow(requireContext()))
                }
            }
            weeklyTimeTable?.addView(row)
        }
    }

    override fun onStart(){
        super.onStart()

        initUI()
    }
}