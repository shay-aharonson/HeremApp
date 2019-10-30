package com.heremapp.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.heremapp.presentation.main.CategoryViewModel
import com.heremapp.ui.binding.BoundViewHolder
import com.heremapp.ui.databinding.ListItemCategoryBinding

/**
 * RecyclerView adapter to display [CategoryViewModel] data to the user.
 */
class CategoriesAdapter : RecyclerView.Adapter<BoundViewHolder<ListItemCategoryBinding>>() {

    private var categories = emptyList<CategoryViewModel>()
    lateinit var onItemClick: ((List<CategoryViewModel>?) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoundViewHolder<ListItemCategoryBinding> {
        return BoundViewHolder.create(parent, ListItemCategoryBinding::inflate)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ListItemCategoryBinding>, position: Int) {
        val category = categories[position]

        holder.binding.apply {
            // Set category name
            categoryName.text = category.category.title
            // Set selection state
            itemCategoryOverlay.isChecked = category.isSelected

            // Load category image
            Glide.with(categoryImage)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .centerCrop()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .load(category.category.icon)
                .into(categoryImage)

            // Root view handles click events
            root.setOnClickListener {
                itemCategoryOverlay.isChecked = !itemCategoryOverlay.isChecked
                category.isSelected = itemCategoryOverlay.isChecked

                onItemClick.invoke(getSelectedItems())
            }
        }
    }

    fun setData(categories: List<CategoryViewModel>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    /**
     * Returns a list of selected categories or null if none are selected.
     */
    private fun getSelectedItems(): List<CategoryViewModel>? {
         categories
             .filter { it.isSelected }
             .also { return if (it.isEmpty()) null else it }
    }
}