package com.wardellbagby.tokipona.model

/**
 * An enum that states the current status of a [Word].
 * @author Wardell
 */
@Suppress("unused") //Will be used in the future. (Date written: 8/2/2017, remove by 2/2/2018)
enum class WordStatus {
    OFFICIAL,
    UNOFFICIAL,
    OBSOLETE,
    POSSIBLY_OBSOLETE,
    CONSIDERED,
    JOKE,
    RESERVED,
    COMMUNITY_INNOVATIONS
}