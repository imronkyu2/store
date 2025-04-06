package com.example.fakestore.ui.keranjang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fakestore.data.local.cart.CartItem
import com.example.fakestore.databinding.ItemCartBinding

class CartItemAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit,
    private val isReadOnly: Boolean = false // ✅ Tambahan flag
) : ListAdapter<CartItem, CartItemAdapter.CartItemViewHolder>(DiffCallback()) {

    class CartItemViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CartItem,
            isReadOnly: Boolean,
            onQuantityChanged: (Int) -> Unit,
            onItemRemoved: () -> Unit
        ) {
            binding.apply {
                Glide.with(root.context)
                    .load(item.image)
                    .into(ivProductImage)

                tvProductTitle.text = item.title
                tvProductPrice.text = "$${item.price}"
                tvQuantity.text = item.quantity.toString()
                tvTotalPrice.text = "$${item.getTotalPrice()}"

                if (isReadOnly) {
                    btnIncrease.visibility = View.GONE
                    btnDecrease.visibility = View.GONE
                    btnRemove.visibility = View.GONE
                } else {
                    btnIncrease.visibility = View.VISIBLE
                    btnDecrease.visibility = View.VISIBLE
                    btnRemove.visibility = View.VISIBLE

                    btnIncrease.setOnClickListener {
                        onQuantityChanged(item.quantity + 1)
                    }
                    btnDecrease.setOnClickListener {
                        if (item.quantity > 1) onQuantityChanged(item.quantity - 1)
                    }
                    btnRemove.setOnClickListener { onItemRemoved() }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.userId == newItem.userId && oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(
            item,
            isReadOnly,
            onQuantityChanged = { newQuantity -> onQuantityChanged(item, newQuantity) },
            onItemRemoved = { onItemRemoved(item) }
        )
    }
}
