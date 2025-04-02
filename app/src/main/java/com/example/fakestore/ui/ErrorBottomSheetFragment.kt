package com.example.fakestore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fakestore.databinding.FragmentErrorBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ErrorBottomSheetFragment(
    private val errorMessage: String,
    private val onResendClick: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentErrorBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErrorBottomSheetBinding.inflate(inflater, container, false)
        binding.tvErrorMessage.text = errorMessage
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnResend.setOnClickListener {
            onResendClick.invoke()
            dismiss()  // Optionally dismiss the bottom sheet after resend
        }
        return binding.root
    }
}