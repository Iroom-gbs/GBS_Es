package com.dayo.executer.data

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
}
