package com.engin.graphqlex.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.api.ApolloResponse
import com.engin.graphqlex.DeleteUserMutation
import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.repository.UserRemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRemoteRepository: UserRemoteRepository) :
    ViewModel() {

    val users = MutableLiveData<List<UsersListQuery.User>>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    private val compositeDisposable = CompositeDisposable()


    /**
     *
     * Getting user list of spaceX
     *
     * @limit limit of User which default 10
     */
    fun getUsers(limit: Int = 10) {
        loading.value = true
        compositeDisposable.add(
            userRemoteRepository.getUserList(limit).subscribeWith(object :
                DisposableSingleObserver<ApolloResponse<UsersListQuery.Data>>() {
                override fun onSuccess(t: ApolloResponse<UsersListQuery.Data>) {
                    loading.postValue(false)
                    users.postValue(t.data!!.users)
                }

                override fun onError(e: Throwable) {
                    loading.postValue(false)
                    error.postValue(true)
                    Log.d("userList", "Sıkıntılı : ${e.toString()}")
                }

            })
        )

    }





    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }


}