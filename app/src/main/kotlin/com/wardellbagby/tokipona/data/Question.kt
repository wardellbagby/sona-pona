package com.wardellbagby.tokipona.data

import android.net.Uri

/**
 * A class representing a displayable question. Implementations decide the data they need to
 * be presented.
 * @author Wardell Bagby
 */
abstract class Question(val answers: List<Answer>)

class DefinitionQuestion(val questionText: String, answers: List<Answer>) : Question(answers) {
    override fun toString(): String {
        return "Text: \"$questionText\"; Answers:$answers"
    }
}

class GlyphQuestion(val questionGlyph: Uri?, answers: List<Answer>) : Question(answers)