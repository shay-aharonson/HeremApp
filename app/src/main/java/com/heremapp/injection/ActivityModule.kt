package com.heremapp.injection

import com.heremapp.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Injection module for the application activities.
 */
@Module
abstract class ActivityModule {

    @ActivityScoped
    @MainScoped
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity
}