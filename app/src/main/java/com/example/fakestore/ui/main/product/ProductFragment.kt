package com.example.fakestore.ui.main.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fakestore.R
import com.example.fakestore.data.model.product.Product
import com.example.fakestore.databinding.FragmentProductBinding
import com.example.fakestore.ui.ErrorBottomSheetFragment
import com.example.fakestore.ui.main.product.adapter.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {
    private lateinit var binding: FragmentProductBinding
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductBinding.bind(view)

        setupRecyclerView()
        observeProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.recyclerViewProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productState.collect { state ->
                when (state) {
                    is ProductState.Loading -> showLoading()
                    is ProductState.Success -> showProducts(state.products)
                    is ProductState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showProducts(products: List<Product>) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewProducts.visibility = View.VISIBLE
        productAdapter.submitList(products)
        productAdapter.notifyDataSetChanged()

        // Debugging
        println("Jumlah Produk: ${products.size}")
        println("RecyclerView Visibility: ${binding.recyclerViewProducts.visibility}")
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        ErrorBottomSheetFragment(
            errorMessage = message,
            onResendClick = { viewModel.refreshProducts() }
        ).show(parentFragmentManager, "ProductErrorBottomSheet")
    }
}