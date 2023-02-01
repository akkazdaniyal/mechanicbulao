package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mechanicsideapp.fragments.ActivitiesFragment
import com.example.mechanicsideapp.fragments.HomescreenFragment
import com.example.mechanicsideapp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class DashboardmechanicActivity : AppCompatActivity() {

    private val homescreenFragment = HomescreenFragment()
    private val activityFragment = ActivitiesFragment()
    private  val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboardmechanic)
        replaceFragment(homescreenFragment)

        val bottomNavigation = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_homescreen -> replaceFragment(homescreenFragment)
                R.id.ic_activity -> replaceFragment(activityFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,fragment)
            transaction.commit()
        }
    }

}