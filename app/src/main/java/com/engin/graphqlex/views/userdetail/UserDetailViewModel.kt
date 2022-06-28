package com.engin.graphqlex.views.userdetail

import androidx.lifecycle.ViewModel
import com.engin.graphqlex.mvibase.IViewModel
import com.engin.graphqlex.utils.notOfType
import com.engin.graphqlex.views.userhome.UserHomeFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject


/**
 * Listens to user actions from the UI ([UserDetailFragment]), retrieves the data and updates the
 * UI as required.
 *
 * @property actionProcessorHolder Contains and executes the business logic of all emitted
 * actions.
 */
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val actionProcessorHolder: UserDetailActionProcessHolder
) : ViewModel(), IViewModel<UserDetailIntent, UserDetailState> {

    /**
     * Proxy subject used to keep the stream alive even after the UI gets recycled.
     * This is basically used to keep ongoing events and the last cached State alive
     * while the UI disconnects and reconnects on config changes.
     */
    private val intentSubject: PublishSubject<UserDetailIntent> = PublishSubject.create()
    private val statesObservable: Observable<UserDetailState> = compose()

    override fun processIntents(intents: Observable<UserDetailIntent>) {
        intents.subscribe(intentSubject)
    }

    override fun states() = statesObservable

    private val intentFilterForUserDetail: ObservableTransformer<UserDetailIntent, UserDetailIntent>
        get() = ObservableTransformer { intents ->
            intents.publish() { shared ->
                Observable.merge(
                    shared.ofType(UserDetailIntent.LoadUserIntent::class.java),
                    shared.notOfType(UserDetailIntent.LoadUserIntent::class.java)
                )
            }
        }

    private val intentFilterForDeleteUser: ObservableTransformer<UserDetailIntent, UserDetailIntent>
        get() = ObservableTransformer { intents ->
            intents.publish() { shared ->
                Observable.merge(
                    shared.ofType(UserDetailIntent.DeleteUserIntent::class.java),
                    shared.notOfType(UserDetailIntent.DeleteUserIntent::class.java)
                )
            }
        }

    /**
     * Compose all components to create the stream logic
     */
    fun compose(): Observable<UserDetailState> {
        return intentSubject
            .compose(intentFilterForUserDetail)
            .compose(intentFilterForDeleteUser)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            // Cache each state and pass it to the reducer to create a new state from
            // the previous cached one and the latest Result emitted from the action processor.
            // The Scan operator is used here for the caching.
            .scan(UserDetailState.idle(), reducer)
            // When a reducer just emits previousState, there's no reason to call render. In fact,
            // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
            // by showing the same snackbar twice in rapid succession).
            .distinctUntilChanged()
            .replay(1)
            // Create the stream on creation without waiting for anyone to subscribe
            // This allows the stream to stay alive even when the UI disconnects and
            // match the stream's lifecycle to the ViewModel's one.
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: UserDetailIntent): UserDetailAction {
        return when (intent) {
            is UserDetailIntent.LoadUserIntent -> UserDetailAction.LoadUserAction(intent.userID)
            is UserDetailIntent.DeleteUserIntent -> UserDetailAction.DeleteUserAction(intent.userId)
        }
    }

    companion object {
        /**
         * The Reducer is where [IState], that the [IView] will use to
         * render itself, are created.
         * It takes the last cached [IState], the latest [IResult] and
         * creates a new [IState] by only updating the related fields.
         * This is basically like a big switch statement of all possible types for the [IResult]
         */
        private val reducer =
            BiFunction { previousState:UserDetailState, result:UserDetailResult ->
                when (result) {
                    is UserDetailResult.LoadUserResult -> when (result) {
                        is UserDetailResult.LoadUserResult.Success -> {
                            previousState.copy(
                                isLoading = false,
                                user =result.user,
                                deleteData = null,
                                error = null
                            )
                        }
                        is UserDetailResult.LoadUserResult.Error -> {
                            previousState.copy(isLoading = false, error = result.throwable)
                        }
                        is UserDetailResult.LoadUserResult.Loading -> {
                            previousState.copy(isLoading = true, error = null)
                        }
                    }
                    is UserDetailResult.DeleteUserResult -> when (result) {
                        is UserDetailResult.DeleteUserResult.Success -> {
                            previousState.copy(
                                isLoading = false,
                                deleteData = result.data,
                                error = null,
                                user = null,
                            )
                        }
                        is UserDetailResult.DeleteUserResult.Loading -> {
                            previousState.copy(isLoading = true, error = null)

                        }
                        is UserDetailResult.DeleteUserResult.Error -> {
                            previousState.copy(isLoading = false, error = result.throwable)
                        }
                    }
                }
            }
    }
}