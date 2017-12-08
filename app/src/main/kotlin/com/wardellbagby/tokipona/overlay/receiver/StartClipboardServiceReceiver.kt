package com.wardellbagby.tokipona.overlay.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.wardellbagby.tokipona.overlay.service.TokiPonaClipboardService

class StartClipboardServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(Intent(context, TokiPonaClipboardService::class.java))
        }
    }
}
