package com.heremapp.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heremapp.presentation.main.PlaceViewModel
import com.heremapp.ui.binding.BoundViewHolder
import com.heremapp.ui.databinding.ListItemPlaceBinding

/**
 * RecyclerView adapter to display [PlaceViewModel] data to the user.
 */
class PlacesAdapter : RecyclerView.Adapter<BoundViewHolder<ListItemPlaceBinding>>() {

    private var places = emptyList<PlaceViewModel>()
    lateinit var onItemClick: (PlaceViewModel) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoundViewHolder<ListItemPlaceBinding> {
        return BoundViewHolder.create(parent, ListItemPlaceBinding::inflate)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: BoundViewHolder<ListItemPlaceBinding>, position: Int) {
        places[position].let { placeViewModel ->
            holder.binding.apply {
                this.placeViewModel = placeViewModel
                this.root.setOnClickListener {
                    onItemClick.invoke(placeViewModel)
                }
            }
        }
    }

    fun setData(categories: List<PlaceViewModel>) {
        this.places = categories
        notifyDataSetChanged()
    }
}