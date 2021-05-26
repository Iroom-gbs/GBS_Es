package com.dayo.executer.data

import android.content.Context
import android.widget.TableRow
import android.widget.TextView

data class MealData(val menu: String, val allergy: List<Boolean>){
    companion object {
        val allFalseList = listOf(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false)
        fun stringToMealData(s: String): MealData{
            val dat = s.split('`')
            if(dat.size == 1){
                return MealData(dat[0], allFalseList)
            }
            val l = mutableListOf<Boolean>()
            l.addAll(allFalseList)
            for(x in dat[1].split('.')){
                if(x=="") continue
                l[x.toInt() - 1] = true
            }
            return MealData(dat[0], l)
        }
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
}
