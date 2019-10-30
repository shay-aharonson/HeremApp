package com.heremapp.injection

import com.heremapp.favorites.FavoritesFragment
import com.heremapp.main.DefaultMainNavigator
import com.heremapp.main.MainActivity
import com.heremapp.map.MapFragment
import com.heremapp.presentation.main.MainNavigator
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.search.BottomSheetNavigator
import com.heremapp.presentation.search.BottomSheetViewModel
import com.heremapp.search.DefaultBottomSheetNavigator
import com.heremapp.search.PlaceFragment
import com.heremapp.search.PlacesFragment
import com.heremapp.search.SearchFragment
import com.heremapp.ui.base.BaseActivity
import com.heremapp.ui.uielements.listeners.OnSwipeGestureDetector
import com.trello.rxlifecycle2.android.ActivityEvent
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.reactivex.Observable

/**
 * MainActivity specific module, provides access to common shared MainActivity and Activity scoped components.
 */
@Module(includes = [MainActivityModule.FragmentBindings::class, NavigationModule::class])
class MainActivityModule {

    /**
     * Submodules for the fragments of [MainActivity]
     */
    @Module
    interface FragmentBindings {
        @ContributesAndroidInjector fun bindMapFragment(): MapFragment
        @ContributesAndroidInjector fun bindSearchFragment(): SearchFragment
        @ContributesAndroidInjector fun bindFavoritesFragment(): FavoritesFragment
        @ContributesAndroidInjector fun bindPlaceFragment(): PlaceFragment
        @ContributesAndroidInjector fun bindPlacesFragment(): PlacesFragment
    }

    @Provides
    @MainScoped
    fun provideBottomSheetNavigator(activity: MainActivity): BottomSheetNavigator {
        return DefaultBottomSheetNavigator(activity)
    }

    @Provides
    @MainScoped
    fun provideBottomSheetViewModel(lifecycle: Observable<ActivityEvent>, mainViewModel: MainViewModel,
                                    navigator: BottomSheetNavigator): BottomSheetViewModel {
        return BottomSheetViewModel(lifecycle, mainViewModel, navigator)
    }

    @Provides
    @MainScoped
    fun provideMainNavigator(activity: MainActivity): MainNavigator {
        return DefaultMainNavigator(activity)
    }


    @Provides
    @MainScoped
    fun provideMainViewModel(): MainViewModel {
        return MainViewModel()
    }

    @Provides
    @ActivityScoped
    fun provideBaseActivity(activity: MainActivity): BaseActivity {
        return activity
    }

    @Provides
    @MainScoped
    fun provideLifecycle(activity: MainActivity): Observable<ActivityEvent> {
        return activity.lifecycle()
    }

    @Provides
    @MainScoped
    fun provideOnSwipeDetector(): OnSwipeGestureDetector {
        return OnSwipeGestureDetector()
    }
}