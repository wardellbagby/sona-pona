package com.wardellbagby.tokipona.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wardellbagby.tokipona.service.TokiPonaClipboardService

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            context.startService(Intent(context, TokiPonaClipboardService::class.java))
        }
    }
}
