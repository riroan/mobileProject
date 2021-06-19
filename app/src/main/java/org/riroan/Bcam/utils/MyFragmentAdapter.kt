package org.riroan.Bcam.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class MyFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return NUM_ITEMS
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return LensFragment()
            1 -> return StickerFragment()
            2 -> return ChromakeyFragment()
            3 -> return ColorFragment()
        }
        return LensFragment()
    }

    companion object {
        const val NUM_ITEMS = 4
    }
}