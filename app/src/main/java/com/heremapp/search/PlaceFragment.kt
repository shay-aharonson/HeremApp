package com.heremapp.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.heremapp.R
import com.heremapp.data.PersistedDataStore
import com.heremapp.databinding.FragmentPlaceBinding
import com.heremapp.presentation.base.BaseToolbarViewModel
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.search.BottomSheetViewModel
import com.heremapp.presentation.search.PlaceFragmentViewModel
import com.heremapp.ui.uielements.listeners.TextChangedListener
import com.trello.rxlifecycle2.components.support.RxFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Displays the information of the currently selected place.
 */
class PlaceFragment : RxFragment() {

    companion object {
        const val TAG = "PlaceFragment"
    }

    @Inject
    internal lateinit var dataStore: PersistedDataStore

    @Inject
    internal lateinit var bottomSheetViewModel: BottomSheetViewModel

    @Inject
    internal lateinit var mainViewModel: MainViewModel

    @Inject
    internal lateinit var toolbarViewModel: BaseToolbarViewModel

    private lateinit var binding: FragmentPlaceBinding

    private val viewModel: PlaceFragmentViewModel by lazy {
        PlaceFragmentViewModel(mainViewModel, lifecycle())
            .apply {
                place.onChanged { place ->
                    if (place != null) binding.placeViewModel = place
                }
            }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_place, null, true)
        binding.placeViewToolbar.setViewModel(toolbarViewModel)
        binding.placeNotesText.addTextChangedListener(TextChangedListener(viewModel::onNotesEdited))
        binding.viewModel = viewModel
        return binding.root
    }
}