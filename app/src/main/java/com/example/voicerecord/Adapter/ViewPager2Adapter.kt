package com.example.voicerecord.Adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(val fa: FragmentActivity): FragmentStateAdapter(fa) {
     val fragmentlist=ArrayList<Fragment>()
    override fun getItemCount()=fragmentlist.size


    fun addFragment(f:Fragment) {
        fragmentlist.add(f)
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentlist.get(position)
    }
}