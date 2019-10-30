package com.heremapp.search

import com.heremapp.R
import com.heremapp.presentation.search.BottomSheetNavigator
import com.heremapp.ui.base.BaseActivity
import com.heremapp.utility.messaging.MessageHandler.Companion.log

/**
 * Default implementation of the [BottomSheetNavigator].
 */
class DefaultBottomSheetNavigator(private val activity: BaseActivity) : BottomSheetNavigator {

    /**
     * If not currently displayed (can be determined by backstack count), replaces the places layout and the transition
     * is added to the backstack.
     */
    override fun showPlace() {
        val fragment = activity.supportFragmentManager.findFragmentByTag(PlaceFragment.TAG) ?: PlaceFragment()
        val fragmentManager = activity.supportFragmentManager

        // Places layout is displayed.
        if (fragmentManager.backStackEntryCount == 0) {
            log("Replacing PlacesFragment with PlaceFragment")
            activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                .replace(R.id.bottom_sheet_fragment_container, fragment, PlaceFragment.TAG)
                .addToBackStack(PlaceFragment.TAG)
                .commit()
        }
    }

    /**
     * Displays the places layout, by replacing the container content or popping the backstack to swap back from the
     * place layout.
     */
    override fun showPlaces() {
        val fragment = activity.supportFragmentManager.findFragmentByTag(PlacesFragment.TAG) ?: PlacesFragment()
        val fragmentManager = activity.supportFragmentManager

        // Place layout is displayed.
        if (fragmentManager.backStackEntryCount > 0) {
            val entry = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1)

            if (entry.name == PlaceFragment.TAG)
                activity.supportFragmentManager.popBackStack()

            // Nothing is displayed.
        } else if (!fragment.isAdded) {
            log("Adding PlacesFragment")

            fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.bottom_sheet_fragment_container, fragment, PlacesFragment.TAG)
                .commit()
        }
    }
}