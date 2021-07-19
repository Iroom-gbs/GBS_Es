package com.dayo.executer.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import com.dayo.executer.R
import com.dayo.executer.data.DataManager
import com.dayo.executer.data.NewHomeTimeTableRow


class TodayInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_info, container, false)
    }
    override fun onStart() {
        super.onStart()
        view?.let { view ->
            val dailyTimeTable = view.findViewById<TableLayout>(R.id.home_daily_timetable)
            for(d in DataManager.timeTableData){
                dailyTimeTable.addView(NewHomeTimeTableRow(requireContext(), d))
            }
        }
    }
}