package com.wardellbagby.tokipona.model

import android.os.Parcel
import android.os.Parcelable

/**
 * An object for a Word. A Word should have:
 *
 * 1. A name (e.g., "toki")
 * 2. A list of [Definition]
 * 3. An optional gloss that is as close to a direct translation for it.
 *
 * @constructor Creates an empty word with a blank name, empty definitions, and a blank gloss.
 *
 * @author Wardell Bagby
 */

data class Word(val name: String = "", val definitions: List<Definition> = listOf(), val gloss: String? = name) : Parcelable {
    companion object {
        @Suppress("unused") // Used by the Android system to un-parcel this object.
        @JvmField val CREATOR: Parcelable.Creator<Word> = object : Parcelable.Creator<Word> {
            override fun createFromParcel(source: Parcel): Word = Word(source)
            override fun newArray(size: Int): Array<Word?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            ArrayList<Definition>().apply { source.readList(this, Definition::class.java.classLoader) },
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeList(definitions)
        dest.writeString(gloss)
    }

    override fun toString(): String {
        return name
    }
}
