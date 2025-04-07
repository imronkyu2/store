package com.example.fakestore.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.fakestore.data.model.product.Product
import com.example.fakestore.data.local.cart.CartRepository
import com.example.fakestore.databinding.ActivityProductDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    @Inject lateinit var cartRepository: CartRepository
    private lateinit var currentProduct: Product
    private var currentQuantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("PRODUCT_EXTRA", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("PRODUCT_EXTRA")
        }

        product?.let {
            currentProduct = it
            setupProductDetails(it)
            checkCartQuantity(it.id)
            setupCartControls(it)
        }
    }

    private fun setupProductDetails(product: Product) {
        binding.apply {
            textTitle.text = product.title
            textPrice.text = "$${product.price}"
            textDescription.text = product.description
            textCategory.text = product.category
            textRating.text = "${product.rating.rate} (${product.rating.count} reviews)"

            Glide.with(this@ProductDetailActivity)
                .load(product.image)
                .into(imageProduct)
        }
    }

    private fun checkCartQuantity(productId: Int) {
        lifecycleScope.launch {
            currentQuantity = cartRepository.getCartItemQuantity(productId)
            updateCartUI()
        }
    }

    private fun setupCartControls(product: Product) {
        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnAddToCart.setOnClickListener {
            lifecycleScope.launch {
                cartRepository.addToCart(product)
                currentQuantity = 1
                updateCartUI()
                Toast.makeText(
                    this@ProductDetailActivity,
                    "${product.title} added to cart",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.addBtn.setOnClickListener {
            lifecycleScope.launch {
                currentQuantity++
                cartRepository.updateCartItemQuantity(product.id, currentQuantity)
                updateCartUI()
            }
        }

        binding.subtractBtn.setOnClickListener {
            lifecycleScope.launch {
                if (currentQuantity > 1) {
                    currentQuantity--
                    cartRepository.updateCartItemQuantity(product.id, currentQuantity)
                } else {
                    cartRepository.removeFromCart(product.id)
                    currentQuantity = 0
                }
                updateCartUI()
            }
        }
    }

    private fun updateCartUI() {
        binding.apply {
            if (currentQuantity > 0) {
                btnAddToCart.visibility = View.GONE
                layoutButton.visibility = View.VISIBLE
                cartQty.text = currentQuantity.toString()
            } else {
                btnAddToCart.visibility = View.VISIBLE
                layoutButton.visibility = View.GONE
            }
        }
    }
}