package com.wardellbagby.tokipona

import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.provider.GlyphContentProvider
import com.wardellbagby.tokipona.util.Words
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.io.FileInputStream

/**
 * @author Wardell Bagby
 */
class WordListValidityCheck {

    @Test
    fun testWordListValidity() {
        //Direct linking is bad, but use the real file so that this test is always in sync.
        var file = File("app/src/main/assets/")
        if (!file.exists()) {
            //We're running from gradlew, so we start in our module folder.
            file = File("src/main/assets/")
            if (!file.exists()) {
                fail("Couldn't find word list file. Current directory is: " + File("").absolutePath)
            }
        }
        val wordListFile = File(file, "word_list.json")
        if (!wordListFile.exists()) return fail("word_list.json was not found in ${file.absolutePath}")
        val stream = FileInputStream(wordListFile)
        val expectedWordCount = 123
        val words = Words.getWordsSync(stream)
        assertTrue("Received an empty list of words.", words.isNotEmpty())
        assertTrue("Expected at least $expectedWordCount words. Received ${words.size}", words.size >= expectedWordCount)
        words.forEach {
            checkGloss(it)
            checkGlyph(file, it)
        }

    }

    private fun checkGloss(word: Word) {
        assertFalse("${word.name} does not have a gloss associated with it.", word.gloss.isNullOrBlank())
    }

    private fun checkGlyph(assetsDir: File, word: Word) {
        val glyphDir = File(assetsDir, GlyphContentProvider.GLYPH_ASSET_PATH)
        assertTrue("\"${word.name}.png\" was not found in ${assetsDir.absolutePath}", File(glyphDir, "${word.name}.png").exists())
    }
}