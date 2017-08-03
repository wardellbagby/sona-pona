package com.wardellbagby.tokipona.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * An object representing a definition for a [Word]
 *
 * Definitions must have a [PartOfSpeech] which will say what part of speech this definition is for.
 * Definitions must also have a definition text, which is just a string that you would expect to
 * see in a normal dictionary.
 *
 * @constructor Creates a Definition using the provided [PartOfSpeech] and definition text.
 *
 * @author Wardell
 */
data class Definition(@SerializedName("part_of_speech") val partOfSpeech: PartOfSpeech,
                      @SerializedName("definition") val definitionText: String) : Parcelable {
    companion object {
        @Suppress("unused") // Used by the Android system to un-parcel this object.
        @JvmField val CREATOR: Parcelable.Creator<Definition> = object : Parcelable.Creator<Definition> {
            override fun createFromParcel(source: Parcel): Definition = Definition(source)
            override fun newArray(size: Int): Array<Definition?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            PartOfSpeech.values()[source.readInt()],
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(partOfSpeech.ordinal)
        dest.writeString(definitionText)
    }
}