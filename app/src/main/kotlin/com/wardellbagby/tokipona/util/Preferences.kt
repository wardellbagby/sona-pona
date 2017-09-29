package com.wardellbagby.tokipona.util

import android.content.SharedPreferences

/**
 * Provides functions to easily set and retrieve user preferences related to this app.
 *
 * @author Wardell Bagby
 */
class Preferences(private val preferences: SharedPreferences) {

    /**
     * Returns whether or not the Overlay Permission dialog should be shown.
     */
    fun shouldShowOverlayPermission(): Boolean {
        return preferences.getBoolean(SHOULD_SHOW_OVERLAY_PERMISSION, true)
    }

    /**
     * Sets whether or not the Overlay Permission dialog should be shown.
     */
    fun setShouldShowOverlayPermission(shouldShowOverlayPermission: Boolean) {
        preferences.edit()
                .putBoolean(SHOULD_SHOW_OVERLAY_PERMISSION, shouldShowOverlayPermission)
                .apply()
    }

    companion object {
        private val SHOULD_SHOW_OVERLAY_PERMISSION = "shouldShowOverlayPermission"
    }
}