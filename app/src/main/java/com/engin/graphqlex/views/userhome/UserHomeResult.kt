package com.engin.graphqlex.views.userhome

import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.mvibase.IResult

sealed class UserHomeResult: IResult{
    sealed class LoadListResult : UserHomeResult() {
        data class Success(val userList: MutableList<UsersListQuery.User>) : LoadListResult()
        data class Error(val throwable: Throwable) : LoadListResult()
        object Loading : LoadListResult()
    }
    sealed class EmptyResult : UserHomeResult() {
        data class Success(val userList: MutableList<UsersListQuery.User>) : EmptyResult()
        data class Error(val throwable: Throwable) : EmptyResult()
        object Loading : EmptyResult()
    }
}
