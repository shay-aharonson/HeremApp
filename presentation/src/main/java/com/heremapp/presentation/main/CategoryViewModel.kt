package com.heremapp.presentation.main

import com.heremapp.data.models.Category

/**
 * ViewModel for a category item, contains category data and whether or not the item has been selected by the user.
 */
class CategoryViewModel(val category: Category) {
    var isSelected: Boolean = false
}