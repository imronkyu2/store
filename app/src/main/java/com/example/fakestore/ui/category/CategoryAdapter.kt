package com.example.fakestore.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fakestore.R
import com.example.fakestore.data.local.category.CategoryEntity
import com.example.fakestore.databinding.ItemCategoryFilterBinding

class CategoryAdapter(
    private val onCheckedChange: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var _categories = mutableListOf<CategoryEntity>()
    val currentList: List<CategoryEntity> get() = _categories

    fun submitList(newList: List<CategoryEntity>) {
        _categories.clear()
        _categories.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_filter, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(_categories[position])
    }

    override fun getItemCount() = _categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCategoryFilterBinding.bind(itemView)

        fun bind(category: CategoryEntity) {
            binding.tvCategory.text = category.name
            binding.checkbox.isChecked = category.isChecked

            binding.root.setOnClickListener {
                binding.checkbox.toggle()
                onCheckedChange(category.id, binding.checkbox.isChecked)
            }

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(category.id, isChecked)
            }
        }
    }
}