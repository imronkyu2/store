package com.example.fakestore.util.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fakestore.R
import com.example.fakestore.databinding.FragmentErrorBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ErrorBottomSheetFragment(
    private val errorMessage: String,
    private val onResendClick: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentErrorBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set style untuk memastikan tinggi BottomSheet sesuai
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvErrorMessage.text = errorMessage
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnResend.setOnClickListener {
            onResendClick()
            dismiss()
        }

        // Atur behavior untuk menghindari BottomNavigationView
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }
}