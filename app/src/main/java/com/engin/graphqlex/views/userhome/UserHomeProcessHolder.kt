package com.engin.graphqlex.views.userhome

import com.engin.graphqlex.mvibase.IAction
import com.engin.graphqlex.repository.UserRemoteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.IllegalStateException
import javax.inject.Inject

/**
 *
 * Contains and executes the business logic for all emitted [IAction]
 * and returns one unique [Observable] of [IResult].
 *
 * This could have been included inside the [IViewModel]
 *
 */
class UserHomeProcessHolder @Inject constructor(private val userRemoteRepository: UserRemoteRepository) {
    private val userListProcessor = ObservableTransformer<UserHomeAction.LoadListAction,UserHomeResult.LoadListResult> { upstream ->
        upstream.flatMap {
            userRemoteRepository.getUserList()
                // Transform the Single to an Observable
                .toObservable()
                // Wrap returned data into an immutable object
                .map { users-> UserHomeResult.LoadListResult.Success(users.data?.users!!.toMutableList()) }
                .cast(UserHomeResult.LoadListResult::class.java)
                // Wrap any error into an immutable object and pass it down the stream
                // without crashing.
                // Because errors are data and hence, should just be part of the stream.
                .onErrorReturn(UserHomeResult.LoadListResult::Error)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(Observable.just(UserHomeResult.LoadListResult.Loading))
        }
    }

    private val emptyProcessor = ObservableTransformer<UserHomeAction.EmptyAction,UserHomeResult.EmptyResult> { upstream ->
        upstream.flatMap {
            userRemoteRepository.getUserList()
                // Transform the Single to an Observable
                .toObservable()
                // Wrap returned data into an immutable object
                .map { users-> UserHomeResult.EmptyResult.Success(users.data?.users!!.toMutableList()) }
                .cast(UserHomeResult.EmptyResult::class.java)
                // Wrap any error into an immutable object and pass it down the stream
                // without crashing.
                // Because errors are data and hence, should just be part of the stream.
                .onErrorReturn(UserHomeResult.EmptyResult::Error)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(Observable.just(UserHomeResult.EmptyResult.Loading))
        }
    }
    /**
     *
     * Splits the [Observable] to match each type of [IAction] to
     * its corresponding business logic processor. Each processor takes a defined [IAction],
     * returns a defined [IResult]
     * The global actionProcessor then merges all [Observable] back to
     * one unique [Observable].
     *
     */
    internal var actionProcessor =ObservableTransformer<UserHomeAction,UserHomeResult>{ upstream: Observable<UserHomeAction> ->
        upstream.publish { shared->
            Observable.merge(
                shared.ofType(UserHomeAction.EmptyAction::class.java).compose(emptyProcessor),
                // Match LoadListAction to userListProcessor
                shared.ofType(UserHomeAction.LoadListAction::class.java).compose(userListProcessor)
            ).mergeWith(
                // Error for not implemented actions
                shared.filter{
                    it !is UserHomeAction.LoadListAction && it !is UserHomeAction.EmptyAction
                }.flatMap { error->
                    Observable.error<UserHomeResult> {
                        IllegalStateException("Unknown Action type : $error")
                    }

                }
            )
        }
    }
}