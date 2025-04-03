package com.example.fakestore.ui.main.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fakestore.data.model.product.Product
import com.example.fakestore.databinding.AdapterItemProductBinding
class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    inner class ProductViewHolder(private val binding: AdapterItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                textTitle.text = product.title
                textPrice.text = "$${product.price}"
//                Glide.with(root.context).load(product.image).into(ivProduct)

                // Load image using Glide
                Glide.with(root.context)
                    .load(product.image)
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal) // Placeholder saat loading
                    .error(android.R.drawable.stat_notify_error) // Gambar default jika gagal load
                    .into(imageProduct)
            }


            println("RecyclerView Adapter di-set")
            println("Produk: ${product}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = AdapterItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem == newItem
        }
    }
}
