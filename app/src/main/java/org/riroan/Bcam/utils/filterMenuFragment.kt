@file:Suppress("DEPRECATION")

package org.riroan.Bcam.utils

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dialog_filtermenu.*
import org.riroan.Bcam.MainActivity
import org.riroan.Bcam.R

class filterMenuFragment(): BottomSheetDialogFragment() {


    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var viewPager: ViewPager

    override fun onStart() {
        super.onStart()
        bottomSheet =
            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {

                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {

                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }



//    private fun showView(view: View, size: Int) {
//        val params = view.layoutParams
//        params.height = size
//        view.layoutParams = params
//    }
//
//    private fun getActionBarSize(): Int {
//        val styledAttributes =
//            requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
//        return styledAttributes.getDimension(0, 0f).toInt()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myview: View = inflater.inflate(R.layout.dialog_filtermenu, container, false)

        val tabLayout = myview.findViewById<TabLayout>(R.id.myTabLayout)

        viewPager = myview.findViewById(R.id.myViewPager)
        tabLayout.addTab(tabLayout.newTab().setText("LENS"))
        tabLayout.addTab(tabLayout.newTab().setText("STICKER"))
        tabLayout.addTab(tabLayout.newTab().setText("CHROMAKEY"))
        tabLayout.addTab(tabLayout.newTab().setText("COLOR"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL


        val adapter = MyFragmentAdapter(childFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        return myview
    }

}
