package com.heremapp.presentation.base

/**
 * ViewModel for the [BaseToolbarView]
 */
class BaseToolbarViewModel(private val navigator: BaseToolbarViewNavigator) {

    /**
     * React to navigation item click events.
     */
    fun onNavigationItemClicked() {
        navigator.onNavigationItemClicked()
    }
}