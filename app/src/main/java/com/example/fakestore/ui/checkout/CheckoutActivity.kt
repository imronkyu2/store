package com.example.fakestore.ui.checkout

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fakestore.databinding.ActivityCheckoutBinding
import com.example.fakestore.ui.keranjang.adapter.CartItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private val viewModel: CheckoutViewModel by viewModels()
    private lateinit var adapter: CartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeCartItems()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = CartItemAdapter(
            onQuantityChanged = { _, _ -> },
            onItemRemoved = {},
            isReadOnly = true // ✅ sembunyikan tombol
        )

        binding.rvCartItems.apply {
            isNestedScrollingEnabled = false  // ✅ matikan nested scroll
            layoutManager = object : LinearLayoutManager(this@CheckoutActivity) {
                override fun canScrollVertically(): Boolean = false  // ✅ nonaktifkan scroll
            }
            adapter = this@CheckoutActivity.adapter
        }
    }

    private fun observeCartItems() {
        viewModel.cartItems.observe(this) { items ->
            adapter.submitList(items)
            binding.tvTotalItems.text = "Jumlah Barang: ${items.sumOf { it.quantity }}"
            val totalPrice = items.sumOf { it.getTotalPrice() }
            binding.tvTotalHarga.text = "Total Harga: $${"%.2f".format(totalPrice)}"
            binding.tvPengiriman.text = "Pengiriman: Gratis"
            binding.tvBiayaLayanan.text = "Biaya Layanan: $0.00"
            binding.tvTotalTagihan.text = "Total Tagihan: $${"%.2f".format(totalPrice)}"
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnBayarSekarang.setOnClickListener {
            viewModel.clearCart()
            Toast.makeText(this, "Pembayaran berhasil!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
