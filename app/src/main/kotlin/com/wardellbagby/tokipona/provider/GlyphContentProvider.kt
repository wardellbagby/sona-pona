package com.wardellbagby.tokipona.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import com.wardellbagby.tokipona.data.Word
import java.io.File
import java.io.IOException

class GlyphContentProvider : ContentProvider() {

    companion object {
        const val GLYPH_ASSET_PATH = "glyphs"

        fun getUriForWord(word: Word?): Uri? {
            return Uri.parse("content://com.wardellbagby.tokipona.glyph/" + word?.name)
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return "image/png"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun openAssetFile(uri: Uri?, mode: String?): AssetFileDescriptor {
        val assets = context.assets
        val imageName = uri?.lastPathSegment
        return try {
            assets.openFd(GLYPH_ASSET_PATH + File.separatorChar + imageName + ".png")
        } catch (e: IOException) {
            super.openAssetFile(uri, mode)
        }
    }
}
