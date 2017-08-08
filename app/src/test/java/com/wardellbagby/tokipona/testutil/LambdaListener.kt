package com.wardellbagby.tokipona.testutil

/**
 * Lambdas cannot be mocked, but this class offers a way around that. Instead of mocking the lambda,
 * create a mock of this class and verify that [invoked] is called. Then, send a real lambda where
 * it needs to go, but manually call [invoked] on this object. With this, you can verify that
 * your lambda was successfully invoked.
 *
 * Note: If, when running a test, you receive a message similar to this:
 *
 * "java.lang.IllegalStateException: Mockito.any() must not be null"
 *
 * change your LambdaListener to accept an optional type parameter!
 *
 * @author Wardell Bagby
 */
open class LambdaListener<in T> {
    open fun invoked(value: T) {

    }
}