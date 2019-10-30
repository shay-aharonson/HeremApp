package com.heremapp.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.heremapp.R
import com.heremapp.communication.places.HerePlacesService
import com.heremapp.databinding.FragmentSearchBinding
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.search.SearchFragmentViewModel
import com.heremapp.ui.search.CategoriesAdapter
import com.location.LocationService
import com.trello.rxlifecycle2.components.support.RxFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Screen which displays all available categories of points of interest in the nearby proximity, also allows the user to
 * select any number of said categories and discover them on the map.
 */
class SearchFragment : RxFragment() {

    @Inject
    internal lateinit var communicationSupervisorViewModel: CommunicationSupervisorViewModel

    @Inject
    internal lateinit var locationService: LocationService

    @Inject
    internal lateinit var placesService: HerePlacesService

    @Inject
    internal lateinit var mainViewModel: MainViewModel

    internal val viewModel: SearchFragmentViewModel by lazy {
        SearchFragmentViewModel(communicationSupervisorViewModel, placesService, locationService, lifecycle(), mainViewModel)
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: CategoriesAdapter

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search, null, true)
        binding.viewModel = viewModel
        setupViews()
        setupListeners()

        return binding.root
    }

    /**
     * Called by the main navigator.
     */
    fun onDisplay() {
        if (isResumed)
            viewModel.onViewDisplayed()
    }

    private fun setupViews() {
        adapter = CategoriesAdapter()
        adapter.onItemClick = viewModel::onCategorySelected
        binding.categoriesRecyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.categoriesRecyclerView.adapter = adapter
    }

    /**
     * Listen to changes to category data and update the adapter.
     */
    private fun setupListeners() {
        viewModel.categories.onChanged(adapter::setData)
    }
}