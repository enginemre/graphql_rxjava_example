package com.engin.graphqlex.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.api.ApolloResponse
import com.engin.graphqlex.DeleteUserMutation
import com.engin.graphqlex.UserByIdQuery
import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.repository.UserRemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(private val userRemoteRepository: UserRemoteRepository):
    ViewModel(){

    var user = MutableLiveData<UserByIdQuery.Users_by_pk>()
    var loading = MutableLiveData<Boolean>()
    var error = MutableLiveData<Boolean>()
    private val compositeDisposable = CompositeDisposable()

    /**
     * Getting user by id
     * @param id  user ID
     */
    fun getUserById(id: String) {
        compositeDisposable.add(
            userRemoteRepository.getUserById(id).subscribeWith(object :
                DisposableSingleObserver<ApolloResponse<UserByIdQuery.Data>>() {
                override fun onSuccess(t: ApolloResponse<UserByIdQuery.Data>) {
                    loading.postValue(false)
                    user.postValue(t.data?.users_by_pk!!)
                    Log.d("UserById", "Success  ${t.data?.users_by_pk!!.name}")
                }

                override fun onError(e: Throwable) {
                    loading.postValue(false)
                    error.postValue(true)
                    Log.d("UserById", "Failed ${e.toString()}")
                }

            })
        )
    }

    /**
     * Deleting user by ID
     *
     * @param id User id
     *
     */
    fun deleteUser(id: String) {
        loading.postValue(true)
        compositeDisposable.add(userRemoteRepository.deleteUser(id).subscribeWith(object :
            DisposableSingleObserver<ApolloResponse<DeleteUserMutation.Data>>() {
            override fun onSuccess(t: ApolloResponse<DeleteUserMutation.Data>) {
                loading.postValue(false)
                Log.d(
                    "deleteUser",
                    "Success deleted rows  : ${t.data?.delete_users!!.affected_rows.toString()}"
                )
            }

            override fun onError(e: Throwable) {
                loading.postValue(false)
                error.postValue(true)
                Log.d("deleteUser", "Failed ${e.toString()}")
            }
        }))
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}