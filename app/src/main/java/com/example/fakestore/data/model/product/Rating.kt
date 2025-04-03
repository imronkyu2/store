package com.example.fakestore.data.model.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    val rate: Double,
    val count: Int
) : Parcelable