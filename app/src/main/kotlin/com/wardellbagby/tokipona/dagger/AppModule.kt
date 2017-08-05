package com.wardellbagby.tokipona.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.wardellbagby.tokipona.util.Preferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Wardell Bagby
 */
@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun providePreferences(sharedPreferences: SharedPreferences): Preferences {
        return Preferences(sharedPreferences)
    }

}