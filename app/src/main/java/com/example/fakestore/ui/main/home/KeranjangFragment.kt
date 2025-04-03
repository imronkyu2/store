package com.example.fakestore.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fakestore.R
import com.example.fakestore.databinding.FragmentKeranjangBinding

class KeranjangFragment : Fragment(R.layout.fragment_keranjang) {
    private lateinit var binding: FragmentKeranjangBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeranjangBinding.inflate(inflater, container, false)
        return binding.root
    }
}
