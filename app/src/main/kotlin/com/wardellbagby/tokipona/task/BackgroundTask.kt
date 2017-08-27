package com.wardellbagby.tokipona.task

import android.os.AsyncTask

/**
 * @author Wardell Bagby
 */
abstract class BackgroundTask<ParamsT, ProgressT, ResultT> : AsyncTask<ParamsT, ProgressT, BackgroundTask.BackgroundTaskResult<ResultT>>() {

    data class BackgroundTaskResult<out ResultT>(val result: ResultT? = null, val exception: Exception? = null)

    /**
     * Called when this task has been backgrounded and is operating in a new thread. All necessary
     * background should be done here.
     *
     * @param parameters The parameters passed into [execute]
     * @return The result of the background operation, or null if there is no result.
     */
    abstract fun onBackgrounded(vararg parameters: ParamsT?): ResultT?

    /**
     * Called before [onBackgrounded] will be called. This is called on the main thread, so no
     * long running operations should be done here.
     */
    open fun setup() {

    }

    /**
     * Called upon successful completion of the background operation. This is called on the main
     * thread, so no long running operations should be done here.
     *
     * @param result The result of the background operation.
     */
    open fun onResult(result: ResultT?) {

    }

    /**
     * Calls upon an unsuccessful completion of the background operation. This is called on the
     * main thread, so no long running operations should be done here.
     *
     * If any exception is thrown in [onBackgrounded] and this function is not overridden, the
     * exception will be rethrown.
     *
     * @param exception The Exception that was thrown by the background operation.
     */
    open fun onException(exception: Exception) {
        throw exception
    }

    open fun onCanceled(result: ResultT?) {

    }

    open fun onCanceled() {

    }

    final override fun onCancelled() {
        super.onCancelled()
        onCanceled()
    }

    final override fun onCancelled(result: BackgroundTaskResult<ResultT>?) {
        onCanceled(result?.result)
    }

    final override fun onPreExecute() {
        super.onPreExecute()
        setup()
    }

    final override fun doInBackground(vararg parameters: ParamsT): BackgroundTaskResult<ResultT> {
        return try {
            BackgroundTaskResult(result = onBackgrounded(*parameters))
        } catch (e: Exception) {
            BackgroundTaskResult(exception = e)
        }
    }

    final override fun onPostExecute(result: BackgroundTaskResult<ResultT>) {
        super.onPostExecute(result)
        if (result.exception != null) {
            onException(result.exception)
        } else {
            onResult(result.result)
        }
    }
}