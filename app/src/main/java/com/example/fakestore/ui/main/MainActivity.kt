package com.example.fakestore.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fakestore.R
import com.example.fakestore.databinding.ActivityMainBinding
import com.example.fakestore.ui.main.home.KeranjangFragment
import com.example.fakestore.ui.main.product.ProductFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastPosition = 1
    private var currentFragment: Fragment = ProductFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load fragment awal
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, currentFragment, ProductFragment::class.java.simpleName)
            .commit()

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val newFragment: Fragment
            val newPosition: Int

            when (menuItem.itemId) {
                R.id.productFragment -> {
                    newPosition = 1
                    newFragment = ProductFragment()
                }

                R.id.keranjangFragment -> {
                    newPosition = 2
                    newFragment = KeranjangFragment()
                }

                else -> return@setOnItemSelectedListener false
            }

            return@setOnItemSelectedListener loadFragment(newPosition, newFragment)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })
    }

    private fun loadFragment(newPosition: Int, newFragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()

//        if (lastPosition > newPosition) {
//            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
//        } else if (lastPosition < newPosition) {
//            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
//        }

        transaction.replace(R.id.fragment_container, newFragment, newFragment.javaClass.simpleName)
        transaction.commit()

        lastPosition = newPosition
        currentFragment = newFragment
        return true
    }

    private fun handleBackPress() {
        if (lastPosition == 1) {
            finish()
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.bottomNavigationView
            loadFragment(1, ProductFragment())
        }
    }
}
