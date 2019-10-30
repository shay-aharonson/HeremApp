package com.heremapp.injection

import com.heremapp.base.DefaultBaseToolbarNavigator
import com.heremapp.presentation.base.BaseToolbarViewModel
import com.heremapp.presentation.base.BaseToolbarViewNavigator
import com.heremapp.ui.base.BaseActivity
import dagger.Module
import dagger.Provides

/**
 * A navigation module for common application navigation components.
 */
@Module
class NavigationModule {

    @Provides
    @ActivityScoped
    fun provideBaseToolbarNavigator(activity: BaseActivity): BaseToolbarViewNavigator {
        return DefaultBaseToolbarNavigator(activity)
    }

    @Provides
    @ActivityScoped
    fun provideBaseToolbarViewModel(navigator: BaseToolbarViewNavigator): BaseToolbarViewModel {
        return BaseToolbarViewModel(navigator)
    }
}