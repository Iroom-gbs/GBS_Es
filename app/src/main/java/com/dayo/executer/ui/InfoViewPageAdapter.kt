package com.dayo.executer.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class InfoViewPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        // 페이지 개수를 정적 변수로 선언
        private const val NUM_PAGES = 3
    }

    override fun getItemCount():Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            1 -> HomeTimeTableFragment()
            2 -> HomeMealInfoFragment()
            else -> TodayInfoFragment()
        }
    }
}