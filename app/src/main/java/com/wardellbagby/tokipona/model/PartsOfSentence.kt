package com.wardellbagby.tokipona.model

/**
 * @author Wardell Bagby
 */
enum class PartsOfSentence {
    VOCATIVE,
    SUBJECT,
    VERB,
    CONJUNCTION,
    MODIFIER,
    DIRECT_OBJECT,
    ERROR;

    override fun toString(): String {
        return name.toLowerCase().replace('_', ' ').capitalize()
    }


}