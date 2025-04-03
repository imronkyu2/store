package com.example.fakestore.ui.main.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.local.category.CategoryEntity
import com.example.fakestore.data.remote.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _productState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productState: StateFlow<ProductState> = _productState

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

    init {
        getProducts()
        loadCategories()
    }

    private fun getProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .catch { e -> _productState.value = ProductState.Error(e.message ?: "Error") }
                .collect { _productState.value = it }
        }
    }

    fun refreshProducts() {
        getProducts()
    }

    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories()
                .collect { _categories.value = it }
        }
    }

    fun updateCategoryCheckedStatus(categoryId: Int, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateCategoryCheckedStatus(categoryId, isChecked)
            loadCategories() // Refresh the list
        }
    }


    fun clearCategorySelections() {
        viewModelScope.launch {
            // Update all categories to unchecked in database
            repository.clearAllCategorySelections()
            // Refresh the categories list
            loadCategories()
        }
    }



}