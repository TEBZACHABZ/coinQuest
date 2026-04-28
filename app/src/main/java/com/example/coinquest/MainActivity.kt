package com.example.coinquest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.coinquest.databinding.ActivityMainBinding
import com.example.coinquest.ui.fragments.AddExpenseFragment
import com.example.coinquest.ui.fragments.ExpensesFragment
import com.example.coinquest.ui.fragments.GoalsFragment
import com.example.coinquest.ui.fragments.ReportsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_expenses -> replaceFragment(ExpensesFragment())
                R.id.nav_add -> replaceFragment(AddExpenseFragment())
                R.id.nav_goals -> replaceFragment(GoalsFragment())
                R.id.nav_reports -> replaceFragment(ReportsFragment())
            }
            true
        }

        // Default fragment
        if (savedInstanceState == null) {
            replaceFragment(ExpensesFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}