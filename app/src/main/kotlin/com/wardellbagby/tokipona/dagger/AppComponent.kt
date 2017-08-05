package com.wardellbagby.tokipona.dagger

import com.wardellbagby.tokipona.ui.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * @author Wardell Bagby
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}