package com.wardellbagby.tokipona.model

/**
 * An enum that states the current status of a [Word].
 * @author Wardell
 */
@Suppress("unused")
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