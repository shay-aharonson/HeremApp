package com.heremapp.data.models

data class Category(
    val id: String,

    val title: String,

    val icon: String,

    val type: String,

    val href: String,

    val system: String,

    val within: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (other is Category) return other.id == this.id
        return super.equals(other)
    }
}