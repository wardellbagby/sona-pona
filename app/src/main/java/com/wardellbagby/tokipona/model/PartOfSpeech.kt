package com.wardellbagby.tokipona.model

/**
 * An enum representing the parts of speech for a [Definition] of a [Word]
 *
 * @author Wardell Bagby
 */
@SuppressWarnings("SpellCheckingInspection")
enum class PartOfSpeech {
    NOUN, //JAN li pona
    MODIFIER, // jan PONA li nasa.
    SEPARATOR, //tenpo ni LA, mi pona.
    TRANSITIVE_VERB, //must have a direct object. e.g., "jan li OLIN e ona."
    INTRANSITIVE_VERB, //doesn't need a direct object. e.g., "jan li LON."
    INTERJECTION, // a a a!
    PREPOSITION, // jan li pana TAWA sina.
    CONJUNCTION, // sina olin ANU olin e mi?
    KAMA, // A special case that shows how the word is modified when used alongside "kama"
    OTHER; // Something that doesn't quite follow these rules.

    override fun toString(): String {
        return name.toLowerCase().replace('_', ' ').capitalize()
    }


}