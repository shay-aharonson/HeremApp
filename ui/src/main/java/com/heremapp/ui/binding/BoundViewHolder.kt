package com.heremapp.ui.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Generic view holder with data binding.
 */
open class BoundViewHolder<out B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        /**
         * @param inflate Binding classes static inflate method. For example `ListitemXXBinding::inflate`
         */
        fun <B : ViewDataBinding> create(parent: ViewGroup, inflate: (LayoutInflater, ViewGroup, Boolean) -> B
        ): BoundViewHolder<B> {
            val inflater = LayoutInflater.from(parent.context)
            val binding = inflate(inflater, parent, false)
            return BoundViewHolder(binding)
        }
    }
}