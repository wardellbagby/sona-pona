package com.wardellbagby.tokipona.task

import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.testutil.LambdaListener
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.io.FileInputStream

/**
 * @author Wardell Bagby
 */
class LoadWordListTaskTest {

    @Test
    fun testLoadWordListTask() {
        val lambdaListener = Mockito.mock(LambdaListener<List<Word>>()::class.java)
        val callback = { words: List<Word> ->
            lambdaListener.invoked(words)
        }

        //Direct linking is bad, but use the real file so that this test is always in sync.
        var file = File("app/src/main/assets/word_list.json")
        if (!file.exists()) {
            //We're running from gradlew, so we start in our module folder.
            file = File("src/main/assets/word_list.json")
            if (!file.exists()) {
                fail("Couldn't find word list file. Current directory is: " + File("").absolutePath)
            }
        }
        val stream = FileInputStream(file)
        LoadWordListTask(callback).apply {
            onResult(onBackgrounded(stream))
        }

        Mockito.verify(lambdaListener).invoked(Mockito.anyList())
    }
}