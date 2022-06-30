package com.engin.graphqlex.ui.userdetail

import com.engin.graphqlex.base.IAction
import com.engin.graphqlex.data.remote.repositorty.UserRemoteRepository
import com.engin.graphqlex.ui.userdetail.UserDetailAction.LoadUserAction
import com.engin.graphqlex.usecase.DeleteUserUseCase
import com.engin.graphqlex.usecase.GetUserUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
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
class UserDetailActionProcessHolder @Inject constructor(private val deleteUseCase: DeleteUserUseCase,private val getUserUseCase: GetUserUseCase) {
    private val userProcessor =
        ObservableTransformer<LoadUserAction, UserDetailResult.LoadUserResult> { upstream: Observable<LoadUserAction> ->
            upstream.flatMap {
                getUserUseCase.invoke(it.userId)
                    // Wrap returned data into an immutable object
                    .map { user -> UserDetailResult.LoadUserResult.Success(user.data?.users_by_pk!!) }
                    .cast(UserDetailResult.LoadUserResult::class.java)
                    // Wrap any error into an immutable object and pass it down the stream
                    // without crashing.
                    // Because errors are data and hence, should just be part of the stream.
                    .onErrorReturn(UserDetailResult.LoadUserResult::Error)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith(Observable.just(UserDetailResult.LoadUserResult.Loading))
            }
        }

    private val deleteUserProcessor =
        ObservableTransformer<UserDetailAction.DeleteUserAction, UserDetailResult.DeleteUserResult> { upstream: Observable<UserDetailAction.DeleteUserAction> ->
            upstream.flatMap {
                deleteUseCase.invoke(it.userId)
                    .map { deleteData-> UserDetailResult.DeleteUserResult.Success(deleteData.dataAssertNoErrors) }
                    .cast(UserDetailResult.DeleteUserResult::class.java)
                    .onErrorReturn(UserDetailResult.DeleteUserResult::Error)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith(Observable.just(UserDetailResult.DeleteUserResult.Loading))

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
    internal  var actionProcessor = ObservableTransformer<UserDetailAction, UserDetailResult> { upstream: Observable<UserDetailAction> ->
        upstream.publish { shared->
            Observable.merge(
                // Match LoadUserAction to userProcessor
                shared.ofType(LoadUserAction::class.java).compose(userProcessor),
                // Match DeleteUserAction to deleteUserProcessor
                shared.ofType(UserDetailAction.DeleteUserAction::class.java).compose(deleteUserProcessor)
            ).mergeWith(
                // Error for not implemented actions
                shared.filter {
                    it !is LoadUserAction && it !is UserDetailAction.DeleteUserAction
                }.flatMap {error->

                    Observable.error(
                        IllegalStateException("Unknown Action type : $error")
                    )
                }
            )
        }
    }
}