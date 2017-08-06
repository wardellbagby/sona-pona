package com.wardellbagby.tokipona

import android.app.Application
import android.content.Intent
import com.rollbar.android.Rollbar
import com.wardellbagby.tokipona.dagger.AppComponent
import com.wardellbagby.tokipona.dagger.AppModule
import com.wardellbagby.tokipona.dagger.DaggerAppComponent
import com.wardellbagby.tokipona.overlay.service.TokiPonaClipboardService

/**
 * @author Wardell Bagby
 */
class TokiPonaApplication : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        val accessToken: String? = BuildConfig.ROLLBAR_ACCESS_TOKEN
        @Suppress("SENSELESS_COMPARISON") //This is NOT always true/false.
        if (accessToken != null) {
            var flavor = BuildConfig.FLAVOR
            if (flavor.isNullOrEmpty()) {
                flavor = "default"
            }
            Rollbar.init(this, BuildConfig.ROLLBAR_ACCESS_TOKEN, flavor + "/" + BuildConfig.BUILD_TYPE)
        }
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        startService(Intent(this, TokiPonaClipboardService::class.java))
    }
}