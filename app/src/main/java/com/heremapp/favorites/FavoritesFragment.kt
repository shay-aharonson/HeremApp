package com.heremapp.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heremapp.R
import com.heremapp.databinding.FragmentFavoritesBinding
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.favorites.FavoritesFragmentViewModel
import com.heremapp.presentation.main.MainNavigator
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.ui.search.PlacesAdapter
import com.trello.rxlifecycle2.components.support.RxFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Screen to display favorite places.
 */
class FavoritesFragment : RxFragment() {

    @Inject
    internal lateinit var mainViewModel: MainViewModel

    @Inject
    internal lateinit var mainNavigator: MainNavigator

    private lateinit var binding: FragmentFavoritesBinding

    private val adapter = PlacesAdapter()
    private val viewModel: FavoritesFragmentViewModel by lazy {
        FavoritesFragmentViewModel(mainViewModel, mainNavigator, lifecycle())
            .apply {
                places.onChanged { adapter.setData(it ?: emptyList()) }
            }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_favorites, null, true)

        initFavoritesList()

        return binding.root
    }

    /**
     * Initialize the favorites recycler view and adapter, along with a click listener for when an item is selected.
     */
    private fun initFavoritesList() {
        binding.favoritesList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.favoritesList.adapter = adapter.apply {
            onItemClick = viewModel::onPlaceClicked
        }
    }
}