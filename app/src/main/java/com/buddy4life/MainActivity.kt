package com.buddy4life

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null
    private var bottomNavigationView: BottomNavigationView? = null

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.bottom_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.addPostFragment -> {
                    Log.i("HERE", "add post!!!!!")
                    bottomNavigationView?.visibility = View.GONE
                    true
                }

                android.R.id.home -> {
                    Log.i("HERE", "home!!!!!")
                    bottomNavigationView?.visibility = View.GONE
                    true
                }

                else -> navController?.let {
                    Log.i("HERE", "else!!!!!")
                    bottomNavigationView?.visibility = View.VISIBLE
                    NavigationUI.onNavDestinationSelected(menuItem, it)
                } ?: false
            }
        }

        override fun onPrepareMenu(menu: Menu) {
            super.onPrepareMenu(menu)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.navHostMain) as? NavHostFragment
        navController = navHostFragment?.navController
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        navController?.let { NavigationUI.setupWithNavController(bottomNavigationView!!, it) }

        addMenuProvider(menuProvider)
    }
}