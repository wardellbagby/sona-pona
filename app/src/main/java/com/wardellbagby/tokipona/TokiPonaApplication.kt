package com.wardellbagby.tokipona

import android.app.Application
import android.content.Intent
import com.wardellbagby.tokipona.service.TokiPonaClipboardService

/**
 * @author Wardell Bagby
 */
class TokiPonaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, TokiPonaClipboardService::class.java))
    }
}