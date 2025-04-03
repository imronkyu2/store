package com.example.fakestore.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fakestore.databinding.FilterBottomSheetBinding
import com.example.fakestore.ui.product.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    interface FilterListener {
        fun onApplyFilter(selectedCategories: Set<String>)
    }

    private var _binding: FilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private var listener: FilterListener? = null
    private val viewModel: ProductViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButtons()
        observeCategories()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { categoryId, isChecked ->
            viewModel.updateCategoryCheckedStatus(categoryId, isChecked)
        }
        binding.recyclerViewCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                categoryAdapter.submitList(categories)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupButtons() {
        binding.btnReset.setOnClickListener {
            // 1. Clear selections in ViewModel
            viewModel.clearCategorySelections()

            // 2. Immediately notify listener with empty set
            listener?.onApplyFilter(emptySet())

            // 3. Close the bottom sheet
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            val selected = categoryAdapter.currentList
                .filter { it.isChecked }
                .map { it.name }
                .toSet()
            listener?.onApplyFilter(selected)
            dismiss()
        }
    }
    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheet"
    }
}