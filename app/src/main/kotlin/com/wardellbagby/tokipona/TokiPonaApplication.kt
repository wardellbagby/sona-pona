package com.wardellbagby.tokipona

import android.app.Application
import android.content.Intent
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
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        startService(Intent(this, TokiPonaClipboardService::class.java))
    }
}