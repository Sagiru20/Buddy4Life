package com.buddy4life

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.navHostMain) as? NavHostFragment
        navController = navHostFragment?.navController
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView?.setupWithNavController(navController!!)

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.addPostFragment) {
                bottomNavigationView?.visibility = View.GONE
            } else {
                bottomNavigationView?.visibility = View.VISIBLE
            }
        }


        bottomNavigationView?.setOnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.myPostsFragment -> navigateToFragment(R.id.myPostsFragment)
                R.id.postsFragment -> navigateToFragment(R.id.postsFragment)
                R.id.addPostFragment -> navigateToFragment(R.id.addPostFragment)
                R.id.userAccountFragment -> navigateToFragment(R.id.userAccountFragment)
            }
            true

        }


    }

    private fun navigateToFragment(fragmentId: Int) {
        navController?.navigate(fragmentId)
    }


}