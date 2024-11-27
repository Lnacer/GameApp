package com.lucas.mygameapp.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.lucas.mygameapp.R
import com.lucas.mygameapp.databinding.ActivityMainBinding
import com.lucas.mygameapp.model.User
import com.lucas.mygameapp.view.library.LibraryFragment
import com.lucas.mygameapp.view.searchgame.SearchFragment
import com.lucas.mygameapp.view.timeline.TimelineFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var _unselectedTextColor : Int = 0
    private var _selectedTextColor : Int = 0


    companion object {
        lateinit var loggedUser : User
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        _unselectedTextColor = ContextCompat.getColor(applicationContext, R.color.black)
        _selectedTextColor =  ContextCompat.getColor(applicationContext, R.color.colorPrimary)

        updateNavigationBarHeight()

        loadFragment(LibraryFragment(), binding.llLibraryButton)

        setClickableViews()
    }

    private fun setClickableViews() {
        binding.llLibraryButton.setOnClickListener {
            loadFragment(LibraryFragment(), binding.llLibraryButton)
        }

        binding.llSearchButton.setOnClickListener {
            loadFragment(SearchFragment(), binding.llSearchButton)
        }

        binding.llTimelineButton.setOnClickListener {
            loadFragment(TimelineFragment(), binding.llTimelineButton)
        }
    }

    private fun loadFragment(fragment : Fragment, buttonLayout: LinearLayout) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.flMainActivity.id, fragment)
        fragmentTransaction.commit()

        clearSelection()

        (buttonLayout.children.first() as ImageView).setColorFilter(_selectedTextColor)
        (buttonLayout.children.last() as TextView).setTextColor(_selectedTextColor)
    }

    private fun clearSelection() {
        (binding.llLibraryButton.children.first() as ImageView).clearColorFilter()
        (binding.llLibraryButton.children.last() as TextView).setTextColor(_unselectedTextColor)

        (binding.llSearchButton.children.first() as ImageView).clearColorFilter()
        (binding.llSearchButton.children.last() as TextView).setTextColor(_unselectedTextColor)

        (binding.llTimelineButton.children.first() as ImageView).clearColorFilter()
        (binding.llTimelineButton.children.last() as TextView).setTextColor(_unselectedTextColor)
    }

    private fun updateNavigationBarHeight() {
        val navigationBarSize = resources.getIdentifier("navigation_bar_height", "dimen", "android")

        val height = resources.getDimensionPixelSize(navigationBarSize)

        if (height > 0) {
            val navigationBottomMenu = binding.cvNavigationBottomMenu.children.first()

            navigationBottomMenu.updatePadding(bottom = height)
        }

        val statusBarSize = resources.getIdentifier("status_bar_height", "dimen", "android")

        val statusBarHeight = resources.getDimensionPixelSize(statusBarSize)

        if (statusBarHeight > 0) {
            binding.rlMainLayout.updatePadding(top = statusBarHeight)
        }
    }
}