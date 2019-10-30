package com.heremapp.base

import com.heremapp.presentation.base.BaseToolbarViewNavigator
import com.heremapp.ui.base.BaseActivity

class DefaultBaseToolbarNavigator(val activity: BaseActivity) : BaseToolbarViewNavigator {

    /**
     * Whether the navigation item is a back or close, both will be handled by backPress().
     */
    override fun onNavigationItemClicked() {
        activity.onBackPressed()
    }
}