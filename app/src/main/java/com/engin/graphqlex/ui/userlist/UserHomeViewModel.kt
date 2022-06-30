package com.engin.graphqlex.ui.userlist

import androidx.lifecycle.ViewModel
import com.engin.graphqlex.base.IViewModel
import com.engin.graphqlex.app.utils.notOfType
import com.engin.graphqlex.app.states.UserHomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject


/**
 * Listens to user actions from the UI ([UserHomeFragment]), retrieves the data and updates the
 * UI as required.
 *
 * @property actionProcessorHolder Contains and executes the business logic of all emitted
 * actions.
 */
@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val actionProcessorHolder: UserHomeProcessHolder
):ViewModel(), IViewModel<UserHomeIntent, UserHomeState> {

    /**
     * Proxy subject used to keep the stream alive even after the UI gets recycled.
     * This is basically used to keep ongoing events and the last cached State alive
     * while the UI disconnects and reconnects on config changes.
     */
    private val intentSubject: PublishSubject<UserHomeIntent> = PublishSubject.create()
    private val statesObservable: Observable<UserHomeState> = compose()


    companion object {
        /**
         * The Reducer is where [IState], that the [IView] will use to
         * render itself, are created.
         * It takes the last cached [IState], the latest [IResult] and
         * creates a new [IState] by only updating the related fields.
         * This is basically like a big switch statement of all possible types for the [IResult]
         */
        private val reducer =
            BiFunction { previousState: UserHomeState, result: UserHomeResult ->
                when (result) {
                    is UserHomeResult.LoadListResult -> when (result) {
                        is UserHomeResult.LoadListResult.Success -> {
                            previousState.copy(
                                isLoading = false,
                                data = result.userList,
                                error = null
                            )
                        }
                        is UserHomeResult.LoadListResult.Error -> {
                            previousState.copy(isLoading = false, error = result.throwable)
                        }
                        is UserHomeResult.LoadListResult.Loading -> {
                            previousState.copy(isLoading = false, error = null)
                        }
                    }
                    is UserHomeResult.EmptyResult -> when (result) {
                        is UserHomeResult.EmptyResult.Success -> {
                            previousState.copy(
                                isLoading = false,
                                data = result.userList,
                                error = null
                            )
                        }
                        is UserHomeResult.EmptyResult.Error -> {
                            previousState.copy(isLoading = false, error = result.throwable)
                        }
                        is UserHomeResult.EmptyResult.Loading -> {
                            previousState.copy(isLoading = false, error = null)
                        }
                    }
                }
            }

    }

    override fun processIntents(intents: Observable<UserHomeIntent>) {
        intents.subscribe(intentSubject)
    }

    override fun states(): Observable<UserHomeState> =statesObservable

    /**
     * take only the first ever InitialIntent and all intents of other types
     * to avoid reloading data on config changes
     */
    private val intentFilter: ObservableTransformer<UserHomeIntent, UserHomeIntent>
        get() = ObservableTransformer { intents ->
            intents.publish() { shared ->
                Observable.merge(
                    shared.ofType(UserHomeIntent.LoadListIntent::class.java),
                   shared.notOfType(UserHomeIntent.LoadListIntent::class.java)
                )
            }
        }

    /**
     * Compose all components to create the stream logic
     */
    fun compose(): Observable<UserHomeState> {
        return intentSubject
            .compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            // Cache each state and pass it to the reducer to create a new state from
            // the previous cached one and the latest Result emitted from the action processor.
            // The Scan operator is used here for the caching.
            .scan(UserHomeState.idle(), reducer)
            // When a reducer just emits previousState, there's no reason to call render. In fact,
            // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
            // by showing the same snackbar twice in rapid succession).
            .distinctUntilChanged()
            // Emit the last one event of the stream on subscription
            // Useful when a View rebinds to the ViewModel after rotation.
            .replay(1)
            // Create the stream on creation without waiting for anyone to subscribe
            // This allows the stream to stay alive even when the UI disconnects and
            // match the stream's lifecycle to the ViewModel's one.
            .autoConnect(0)
    }

    /**
     * Translate an [IIntent] to an [IAction].
     * Used to decouple the UI and the business logic to allow easy testings and reusability.
     */
    fun actionFromIntent(intent: UserHomeIntent): UserHomeAction {
        return when (intent) {
            is UserHomeIntent.LoadListIntent -> UserHomeAction.LoadListAction
            is UserHomeIntent.EmptyIntent -> UserHomeAction.EmptyAction
        }
    }



}