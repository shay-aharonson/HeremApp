package com.heremapp.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heremapp.R
import com.heremapp.databinding.FragmentPlacesBinding
import com.heremapp.presentation.base.BaseToolbarViewModel
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.search.BottomSheetViewModel
import com.heremapp.presentation.search.PlacesFragmentViewModel
import com.heremapp.ui.search.PlacesAdapter
import com.trello.rxlifecycle2.components.support.RxFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Displays a list of places, ones favorited by the user, followed by all places matching the selected categories and
 * search radius retrieved by the [HerePlacesApi].
 */
class PlacesFragment : RxFragment() {

    companion object {
        const val TAG = "PlacesFragment"
    }

    @Inject
    internal lateinit var bottomSheetViewModel: BottomSheetViewModel

    @Inject
    internal lateinit var mainViewModel: MainViewModel

    @Inject
    internal lateinit var toolbarViewModel: BaseToolbarViewModel

    private lateinit var binding: FragmentPlacesBinding

    private val adapter = PlacesAdapter()
    private val viewModel: PlacesFragmentViewModel by lazy {
        PlacesFragmentViewModel(mainViewModel, lifecycle())
            .apply {
                places.onChanged { adapter.setData(it ?: emptyList()) }
            }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_places, null, true)
        binding.placeViewToolbar.setViewModel(toolbarViewModel)
        initPlacesList()

        return binding.root
    }

    private fun initPlacesList() {
        binding.placesList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.placesList.adapter = adapter.apply {
            onItemClick = viewModel::onPlaceClicked
        }
    }
}