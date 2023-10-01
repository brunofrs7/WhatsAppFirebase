package com.example.whatsappfirebase.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsappfirebase.fragments.ContactosFragment
import com.example.whatsappfirebase.fragments.ConversasFragment

class ViewPagerAdapter(
    private val abas: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return abas.count()
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return ContactosFragment()
        }
        return ConversasFragment()
    }
}