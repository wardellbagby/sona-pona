package com.wardellbagby.tokipona.ui.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.overlay.service.TokiPonaClipboardService
import com.wardellbagby.tokipona.ui.activity.BaseActivity.BaseEvent
import com.wardellbagby.tokipona.util.Fragments
import com.wardellbagby.tokipona.util.sendOnBackPressed
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.FlowableProcessor

/**
 * A Base class that all Activities should extend in order to provide some useful functionality.
 *
 * Currently, this functionality consists of:
 *
 * - Automated saving and restoring of instance state via Pikkel.
 * - A RxJava based communication protocol for Activity to Fragment interaction.
 *
 * In order to use the automated instance state saving, member variables should be initialized via
 * the Pikkel delegate. E.g.,
 *
 * ```
 * class MyActivity: BaseActivity
 *
 *     private var mBool by state(false)
 * ```
 *
 * The BaseActivity and Pikkel will take care of the rest.
 *
 * For Activity to Fragment communication, Activity that extend this should override [getProcessor]
 * and return a valid [FlowableProcessor]. Activities should also create their own implementation
 * of [BaseEvent], as [BaseEvent]s are used to wrap data that should be sent to Fragments.
 *
 * @author Wardell Bagby
 */
@SuppressLint("Registered") //This only provides functionality to Activities that extend it.
abstract class BaseActivity<T : BaseActivity.BaseEvent> : AppCompatActivity(), Pikkel by PikkelDelegate() {

    private var disposables: CompositeDisposable? = null

    abstract class BaseEvent
    @Suppress("unused") //Will be used as more Activities are added. (Date written: 8/4/2017, remove by 2/4/2018)
    class VoidEvent : BaseEvent() //Should this exist or should it be in its own BaseFragmentActivity?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposables = CompositeDisposable()
        restoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        disposables?.dispose()
        unbindService(mConnection)
    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, TokiPonaClipboardService::class.java), mConnection, 0)
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.sendOnBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        findViewById<Toolbar?>(R.id.toolbar)?.title = title
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)
        findViewById<Toolbar?>(R.id.toolbar)?.setTitle(titleId)
    }

    fun <EventType : T> safeSubscribe(consumer: (EventType) -> Unit) {
        val disposable = subscribeWith(consumer)
        if (disposable != null) {
            disposables?.add(disposable)
        }
    }

    open fun getProcessor(): FlowableProcessor<T>? {
        return null
    }

    open fun <EventType : T> subscribeWith(consumer: (EventType) -> Unit): Disposable? {
        return getProcessor()?.subscribe({
            @Suppress("UNCHECKED_CAST") //This will fail safely, and we null-check later.
            val event: EventType? = it as? EventType
            if (event != null) {
                consumer(event)
            }
        })
    }

    fun getSharedElementForTransition(transitionName: String): View? {
        return Fragments.getSharedElementForTransition(window.decorView, supportFragmentManager, transitionName)
    }

    fun replace(@IdRes id: Int, fragmentToAdd: Fragment, tag: String) {
        Fragments.replace(supportFragmentManager, id, fragmentToAdd, tag)
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {}
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {}
    }
}