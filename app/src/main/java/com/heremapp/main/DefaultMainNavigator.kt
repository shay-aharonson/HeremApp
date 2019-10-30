package com.heremapp.main

import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.heremapp.R
import com.heremapp.favorites.FavoritesFragment
import com.heremapp.map.MapFragment
import com.heremapp.presentation.main.MainNavigator
import com.heremapp.presentation.main.MainNavigator.Companion.ScreenType
import com.heremapp.search.SearchFragment
import com.heremapp.ui.base.BaseActivity
import com.heremapp.ui.uielements.components.HereMappBottomNavigationView
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Default implementation of [MainNavigator].
 *
 * Navigation is processed via events originating from the [HereMappBottomNavigationView], upon selected item change.
 * Any programmatic, non user initiated navigation events must be requested by
 * [MainNavigator.requestScreenNavigation] which is observed on [MainNavigator.observeNavigationRequests] by the
 * [HereMappBottomNavigationView] and is processed accordingly.
 */
class DefaultMainNavigator(private val activity: BaseActivity) : MainNavigator {

    private val screenNavigationRequestSubject = PublishSubject.create<@ScreenType Int>()

    private val mapFragment: MapFragment by lazy {
        activity.supportFragmentManager.findFragmentByTag(MapFragment::class.java.name) as MapFragment?
            ?: MapFragment()
    }

    private val searchFragment: SearchFragment by lazy {
        activity.supportFragmentManager.findFragmentByTag(SearchFragment::class.java.name) as SearchFragment?
            ?: SearchFragment()
    }

    private val favoritesFragment: FavoritesFragment by lazy {
        activity.supportFragmentManager.findFragmentByTag(FavoritesFragment::class.java.name) as FavoritesFragment?
            ?: FavoritesFragment()
    }

    private var currentFragment: Fragment? = null

    /**
     * Navigate to the given selected menu item if it differs from the current item.
     */
    override fun navigateToMenuItem(selectedMenuItem: MenuItem, @IdRes currentItem: Int): Boolean {
        log("Navigating to ScreenType: $selectedMenuItem..")
        when (selectedMenuItem.itemId) {
            // If the same tab is selected as currently displayed do nothing.
            currentItem -> return true
            R.id.action_map -> showFragment(mapFragment, MapFragment::class.java.name)
            R.id.action_search -> {
                showFragment(searchFragment, SearchFragment::class.java.name)
                searchFragment.onDisplay()
            }
            R.id.action_favorites -> showFragment(favoritesFragment, FavoritesFragment::class.java.name)
        }
        return true
    }

    /**
     * Request to navigate to the given screen.
     */
    override fun requestScreenNavigation(@ScreenType screen: Int) {
        log("Navigation requested, ScreenType: $screen.")
        screenNavigationRequestSubject.onNext(screen)
    }

    /**
     * Listen to navigation requests. Used by [HereMappBottomNavigationView] to initiate the navigation.
     */
    override fun observeNavigationRequests(): Observable<@ScreenType Int> {
        return screenNavigationRequestSubject
    }

    /**
     * Set the initial display. Should only be called once when the activity is created.
     */
    override fun setInitialScreen() {
        showFragment(mapFragment, MapFragment::class.java.name)
    }

    /**
     * Display the given fragment and hide the previously selected.
     */
    private fun showFragment(fragment: Fragment, tag: String) {
        // Check first to see if it has been added.
        if (!fragment.isAdded)
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, tag)
                .commit()

        // Display the fragment
        activity.supportFragmentManager.beginTransaction()
            .show(fragment)
            .commit()

        // Hide previous fragment.
        currentFragment?.let {
            activity.supportFragmentManager.beginTransaction()
                .hide(it)
                .commit()
        }

        currentFragment = fragment
    }
}