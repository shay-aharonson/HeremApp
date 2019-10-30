package com.heremapp.base

import com.heremapp.injection.DaggerAppComponent
import com.heremapp.utility.injection.ContextModule
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class HeremAppApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .application(this)
            .build()
    }
}