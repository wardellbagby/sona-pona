package com.wardellbagby.tokipona.dagger

import com.wardellbagby.tokipona.overlay.service.TokiPonaClipboardService
import com.wardellbagby.tokipona.ui.activity.MainActivity
import com.wardellbagby.tokipona.ui.fragment.QuizFragment
import dagger.Component
import javax.inject.Singleton

/**
 * @author Wardell Bagby
 */
@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(quizFragment: QuizFragment)
    fun inject(clipboardService: TokiPonaClipboardService)
}