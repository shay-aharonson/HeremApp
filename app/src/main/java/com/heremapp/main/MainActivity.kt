package com.heremapp.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.heremapp.R
import com.heremapp.communication.NetworkSupervisorService
import com.heremapp.communication.geo.HereGeoService
import com.heremapp.databinding.ActivityMainBinding
import com.heremapp.permissions.DefaultPermissionsProvider
import com.heremapp.permissions.DefaultPermissionsProvider.Companion.REQUEST_CODE_PERMISSIONS
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel
import com.heremapp.presentation.main.MainNavigator
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.permissions.RequestPermissionsViewModel
import com.heremapp.presentation.search.BottomSheetViewModel
import com.heremapp.ui.base.BaseActivity
import com.heremapp.ui.uielements.components.HereMappBottomNavigationView
import com.location.LocationService
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


/**
 * Main UI, has a bottom navigation bar (map, category selection and make believe favorites). Hides and shows fragments
 * based on the bottom navigation selection. Fragments are kept in local memory due to the expensive initialization and
 * frequent swapping.
 */
class MainActivity : BaseActivity() , HasSupportFragmentInjector {

    @Inject
    internal lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    internal lateinit var locationService: LocationService

    @Inject
    internal lateinit var networkSupervisorService: NetworkSupervisorService

    @Inject
    internal lateinit var hereGeoService: HereGeoService

    @Inject
    internal lateinit var navigator: MainNavigator

    @Inject
    internal lateinit var mainViewModel: MainViewModel

    @Inject
    internal lateinit var bottomSheetViewModel: BottomSheetViewModel

    @Inject
    internal lateinit var communicationSupervisorViewModel: CommunicationSupervisorViewModel

    private lateinit var binding: ActivityMainBinding

    private val bottomNavigationView: HereMappBottomNavigationView by lazy { binding.bottomNavigation }

    private val permissionsViewModel: RequestPermissionsViewModel by lazy {
        RequestPermissionsViewModel(DefaultPermissionsProvider(this), locationService, networkSupervisorService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
        binding.communicationViewModel = communicationSupervisorViewModel

        addOnPermissionResultCallback(REQUEST_CODE_PERMISSIONS, permissionsViewModel)
        setBottomNavigationBar()
        setBottomSheet()
        navigator.setInitialScreen()
    }

    /**
     * This activity's navigation is built on a single item backstack and bottom sheet structure, as follows:
     * Activity layer > bottom sheet layer with a single fragment backstack.
     * If the bottom sheet is not expanded a back press will close the activity. If the bottom sheet is expanded a
     * back press will attempt to pop the backstack and if that fails it will dismiss the bottom sheet.
     */
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        // Pop back stack if bottom sheet is loaded but not expanded
        if (count > 0 && !bottomSheetViewModel.isVisible())
            supportFragmentManager.popBackStack()

        // Pop back stack if bottom sheet is loaded or bottom sheet fails to dismiss.
        if (count > 0 || !binding.bottomSheet.requestDismiss())
            super.onBackPressed()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    private fun setBottomSheet() {
        binding.bottomSheet.setDependencies(bottomSheetViewModel)
        // Set the bottom sheet as listener for the container's touch events.
        binding.fragmentContainer.bottomSheetListener = binding.bottomSheet
    }

    private fun setBottomNavigationBar() {
        // When the navigation view selected index changes, navigate to the appropriate screen.
        bottomNavigationView.setOnNavigationItemSelectedListener { selectedItem ->
            navigator.navigateToMenuItem(selectedItem, bottomNavigationView.selectedItemId)
        }
        // Set the navigation view as a listener to the navigation requests.
        bottomNavigationView.subscribeToNavigationRequests(navigator.observeNavigationRequests())
    }
}