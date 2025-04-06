// ui/profile/ProfileBottomSheet.kt
package com.example.fakestore.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.fakestore.data.local.prfile.User
import com.example.fakestore.databinding.FragmentProfileBottomSheetBinding
import com.example.fakestore.ui.home.MainActivity
import com.example.fakestore.util.state.ProfileState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentProfileBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.getUserProfile()
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    ProfileState.Loading -> showLoading()
                    is ProfileState.Success -> showProfile(state.user)
                    is ProfileState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun logout() {
        // Clear token dan tutup bottom sheet
        (requireActivity() as? MainActivity)?.logout()
        dismiss()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentGroup.visibility = View.GONE
        binding.errorText.visibility = View.GONE
    }

    private fun showProfile(user: User) {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE

        binding.apply {
            tvName.text = "${user.name.firstname} ${user.name.lastname}"
            tvEmail.text = user.email
            tvUsername.text = user.username
            tvPhone.text = user.phone
            tvAddress.text = "${user.address.street} ${user.address.number}, ${user.address.city}"
        }
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}