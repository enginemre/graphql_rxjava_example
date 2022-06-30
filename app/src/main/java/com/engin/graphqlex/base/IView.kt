package com.engin.graphqlex.base

import io.reactivex.rxjava3.core.Observable

/**
 * Object representing a UI that will
 * a) emit its intents to a view model,
 * b) subscribes to a view model for rendering its UI.
 *
 * @param I Top class of the [IIntent] that the [IView] will be emitting.
 * @param S Top class of the [IState] the [IView] will be subscribing to.
 */
interface IView<I: IIntent,in S: IState> {

    /**
     *
     * listen view
     *
     */
    fun intents():Observable<I>

    /**
     *
     * rendering given state
     *
     */
    fun render(state:S)
}