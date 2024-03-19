package ru.testtask.clockexampleapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.testtask.clockexampleapplication.ui.fragments.FirstFragment
import ru.testtask.clockexampleapplication.ui.fragments.SecondFragment
import ru.testtask.clockexampleapplication.ui.fragments.ThirdFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstFragment= FirstFragment()
        val secondFragment= SecondFragment()
        val thirdFragment= ThirdFragment()

        bottomNavigationView = findViewById(R.id.bottom_nav)

        setCurrentFragment(firstFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.fragment_first->setCurrentFragment(firstFragment)
                R.id.fragment_second->setCurrentFragment(secondFragment)
                R.id.fragment_third->setCurrentFragment(thirdFragment)

            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_frame, fragment)
            commit()
        }
}