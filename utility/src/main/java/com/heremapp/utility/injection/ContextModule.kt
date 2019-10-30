package com.heremapp.utility.injection

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides Application and Application Context.
 */
@Module
class ContextModule @Inject constructor(private val application: Application) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideApplication(): Application {
        return application
    }
}