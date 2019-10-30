package com.heremapp.presentation.main

import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import io.reactivex.Observable

/**
 * Navigation interface for the [MainActivity].
 */
interface MainNavigator {
    companion object {
        const val SCREEN_TYPE_MAP = 1
        const val SCREEN_TYPE_SEARCH = 2
        const val SCREEN_TYPE_FAVORITES = 3

        @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
        @IntDef(
            SCREEN_TYPE_MAP,
            SCREEN_TYPE_SEARCH,
            SCREEN_TYPE_FAVORITES
        )
        annotation class ScreenType
    }

    /**
     * Handle bottom bar menu navigation.
     */
    fun navigateToMenuItem(selectedMenuItem: MenuItem, @IdRes currentItem: Int): Boolean

    /**
     * Submits a request to navigate to the given screen.
     */
    fun requestScreenNavigation(@ScreenType screen: Int)

    /**
     * Listen to navigation requests. Primarily utilized by the [BottomNavigationView].
     */
    fun observeNavigationRequests(): Observable<@ScreenType Int>

    /**
     * Sets the initial screen for the [BottomNavigationView] to display.
     */
    fun setInitialScreen()
}