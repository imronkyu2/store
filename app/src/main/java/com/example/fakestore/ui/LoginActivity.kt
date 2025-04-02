package com.example.fakestore.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fakestore.databinding.ActivityLoginBinding
import com.example.fakestore.util.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Mulai monitoring jaringan
        networkMonitor.startMonitoring()

        setupObservers()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(username, password)
            } else {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is LoginState.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is LoginState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is LoginState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showErrorBottomSheet(state.message) {
                                // Resend login action
                                val username = binding.etUsername.text.toString().trim()
                                val password = binding.etPassword.text.toString().trim()
                                if (username.isNotEmpty() && password.isNotEmpty()) {
                                    viewModel.login(username, password)
                                } else {
                                    Toast.makeText(this@LoginActivity, "Enter username and password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showErrorBottomSheet(errorMessage: String, onResendClick: () -> Unit) {
        val errorBottomSheet = ErrorBottomSheetFragment(errorMessage, onResendClick)
        errorBottomSheet.show(supportFragmentManager, "ErrorBottomSheet")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Berhenti memonitor jaringan saat activity dihancurkan
        networkMonitor.stopMonitoring()
    }
}
