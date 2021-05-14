package com.dayo.executer.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.dayo.executer.*
import com.dayo.executer.data.AblrData
import com.dayo.executer.data.DataManager
import com.dayo.executer.data.MealData
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

    private fun setMealText() {
        val mealText = view?.findViewById<TextView>(R.id.mealText)
        mealText?.setText(MealData.getMealData());
    }

    override fun onStart() {
        super.onStart()

        setMealText()
    }
}