package com.heremapp.ui.uielements.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.heremapp.presentation.main.MainNavigator
import com.heremapp.presentation.main.MainNavigator.Companion.ScreenType
import com.heremapp.ui.R
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import io.reactivex.Observable

/**
 * Bottom navigation view that listens to navigation requests and updates the selected index accordingly.
 */
class HereMappBottomNavigationView(context: Context?, attrs: AttributeSet?) : BottomNavigationView(context, attrs) {

    /**
     * Listen to navigation requests.
     */
    @SuppressLint("CheckResult")
    fun subscribeToNavigationRequests(requestObservable: Observable<@ScreenType Int>) {
        requestObservable
            .subscribe(::handleNavigationRequest, ::log)
    }

    /**
     * Update selected index.
     */
    private fun handleNavigationRequest(@ScreenType screenType: Int) {
        log("Setting selected screen to ScreenType: $screenType..")
        selectedItemId = when (screenType) {
            MainNavigator.SCREEN_TYPE_MAP -> R.id.action_map
            MainNavigator.SCREEN_TYPE_SEARCH -> R.id.action_search
            MainNavigator.SCREEN_TYPE_FAVORITES -> R.id.action_favorites
            else -> throw IllegalArgumentException("Unknown ScreenType: $screenType!")
        }
    }
}