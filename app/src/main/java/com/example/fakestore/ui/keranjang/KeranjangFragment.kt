package com.example.fakestore.ui.keranjang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fakestore.R
import com.example.fakestore.data.local.cart.CartItem
import com.example.fakestore.databinding.FragmentKeranjangBinding
import com.example.fakestore.ui.keranjang.adapter.CartItemAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class KeranjangFragment : Fragment(R.layout.fragment_keranjang) {
    private lateinit var binding: FragmentKeranjangBinding
    private val viewModel: KeranjangViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKeranjangBinding.bind(view)

        val adapter = CartItemAdapter(
            onQuantityChanged = { item, newQuantity ->
                viewModel.updateCartItem(item.copy(quantity = newQuantity))
            },
            onItemRemoved = { item ->
                viewModel.removeFromCart(item)
            }
        )

        binding.apply {
            rvCartItems.layoutManager = LinearLayoutManager(requireContext())
            rvCartItems.adapter = adapter

            btnCheckout.setOnClickListener {
                viewModel.clearCart()
                Toast.makeText(requireContext(), "Checkout successful", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            updateTotalPrice(items)
            binding.tvEmptyCart.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun updateTotalPrice(items: List<CartItem>) {
        val total = items.sumOf { it.getTotalPrice() }
        binding.tvTotalPrice.text = "Total: $${"%.2f".format(total)}"
    }
}