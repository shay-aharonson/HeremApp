package com.heremapp.communication.gson.places

import com.google.gson.annotations.SerializedName
import com.heremapp.data.models.Category

data class CategoriesResponseGson(
    @SerializedName("items")
    val categories: List<Category>
)