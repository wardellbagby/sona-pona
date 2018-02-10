package com.wardellbagby.tokipona.util

import android.content.SharedPreferences

/**
 * Provides functions to easily set and retrieve user preferences related to this app.
 *
 * @author Wardell Bagby
 */
class Preferences(private val preferences: SharedPreferences) {

    /**
     * Whether or not the Overlay Permission dialog should be shown.
     */
    var shouldShowOverlayPermission: Boolean
        get() = preferences.getBoolean(SHOULD_SHOW_OVERLAY_PERMISSION, true)
        set(value) {
            preferences.edit()
                    .putBoolean(SHOULD_SHOW_OVERLAY_PERMISSION, value)
                    .apply()
        }

    /**
     * Whether or not the clipboard service should be enabled.
     */
    var isClipboardServiceEnabled: Boolean
        get() = preferences.getBoolean(IS_CLIPBOARD_SERVICE_ENABLED, false)
        set(value) {
            preferences.edit().putBoolean(IS_CLIPBOARD_SERVICE_ENABLED, value).apply()
        }

    companion object {
        private const val SHOULD_SHOW_OVERLAY_PERMISSION = "shouldShowOverlayPermission"
        private const val IS_CLIPBOARD_SERVICE_ENABLED = "isClipboardServiceEnabled"
    }
}