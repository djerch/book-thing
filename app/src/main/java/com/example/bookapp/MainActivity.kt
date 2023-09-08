package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    // create firebase authentication object
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = SearchFragment()
        val listFragment = ReadingListFragment()
        window.setBackgroundDrawableResource(R.drawable.books3)

        // applying default fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, homeFragment)
            commit()
        }

        // drawer stuff
        drawerLayout = findViewById(R.id.my_drawer_layout)
        navigationView = findViewById(R.id.nav)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBarDrawerToggle.syncState()

        // listener for selecting navigation items
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            drawerLayout.closeDrawer(GravityCompat.START)
            when (id) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, homeFragment)
                        addToBackStack(null)
                        commit()
                        title = "Search"
                    }
                    true
                }
                R.id.nav_list -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, listFragment)
                        addToBackStack(null)
                        title = "My Reading List"
                        commit()
                    }
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    signOut()
                    true
                }
                else -> {
                    false
                }

            }
        }

        // initialize the firebase auth object
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser

        title="book thing"

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // handles the log out functionality
    fun signOut() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}