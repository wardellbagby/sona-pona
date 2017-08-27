package com.wardellbagby.tokipona.util

import android.content.SharedPreferences

/**
 * Provides functions to easily set and retrieve user preferences related to this app.
 *
 * @author Wardell Bagby
 */
class Preferences(private val preferences: SharedPreferences) {

    fun shouldShowOverlayPermission(): Boolean {
        return preferences.getBoolean(SHOULD_SHOW_OVERLAY_PERMISSION, true)
    }

    fun setShouldShowOverlayPermission(shouldShowOverlayPermission: Boolean) {
        preferences.edit()
                .putBoolean(SHOULD_SHOW_OVERLAY_PERMISSION, shouldShowOverlayPermission)
                .apply()
    }

    companion object {
        private val SHOULD_SHOW_OVERLAY_PERMISSION = "shouldShowOverlayPermission"
    }
}