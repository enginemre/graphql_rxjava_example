package com.engin.graphqlex.base

import io.reactivex.rxjava3.core.Observable
/**
 * Object that will subscribes to a [IView]'s [IIntent]s,
 * process it and emit a [IState] back.
 *
 * @param I Top class of the [IIntent] that the [IViewModel] will be subscribing
 * to.
 * @param S Top class of the [IState] the [IViewModel] will be emitting.
 */
interface IViewModel<I: IIntent,S: IState> {
    fun processIntents(intents:Observable<I>)

    fun states():Observable<S>
}