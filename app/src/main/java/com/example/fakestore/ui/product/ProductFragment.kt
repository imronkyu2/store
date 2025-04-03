package com.example.fakestore.ui.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fakestore.R
import com.example.fakestore.data.model.product.Product
import com.example.fakestore.databinding.FragmentProductBinding
import com.example.fakestore.util.bottomsheet.ErrorBottomSheetFragment
import com.example.fakestore.ui.product.adapter.ProductAdapter
import com.example.fakestore.ui.category.FilterBottomSheetFragment
import com.example.fakestore.util.state.ProductState
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
        setupFilterButton()
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

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheet = FilterBottomSheetFragment().apply {
            setFilterListener(object : FilterBottomSheetFragment.FilterListener {
                override fun onApplyFilter(selectedCategories: Set<String>) {
                    // This will be called immediately when reset is clicked
                    filterProducts(selectedCategories)
                }
            })
        }
        bottomSheet.show(parentFragmentManager, FilterBottomSheetFragment.TAG)
    }

    private fun filterProducts(selectedCategories: Set<String>) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productState.collect { state ->
                if (state is ProductState.Success) {
                    val filtered = if (selectedCategories.isEmpty()) {
                        state.products // Show all products when empty
                    } else {
                        state.products.filter {
                            selectedCategories.contains(it.category)
                        }
                    }
                    showProducts(filtered)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showProducts(products: List<Product>) {
        binding.progressBar.visibility = View.GONE
        productAdapter.submitList(products)
        binding.recyclerViewProducts.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        ErrorBottomSheetFragment(message) { viewModel.refreshProducts() }
            .show(parentFragmentManager, "ErrorDialog")
    }
}