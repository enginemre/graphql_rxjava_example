package com.engin.graphqlex.ui.userdetail

import com.engin.graphqlex.DeleteUserMutation
import com.engin.graphqlex.UserByIdQuery
import com.engin.graphqlex.base.IResult

sealed class UserDetailResult : IResult {
    sealed class LoadUserResult: UserDetailResult(){
        object Loading : LoadUserResult()
        data class Success(val user: UserByIdQuery.Users_by_pk) : LoadUserResult()
        data class Error(val throwable: Throwable?) : LoadUserResult()
    }

    sealed class DeleteUserResult: UserDetailResult(){
        object Loading : DeleteUserResult()
        data class Success(val data: DeleteUserMutation.Data) : DeleteUserResult()
        data class Error(val throwable: Throwable?) : DeleteUserResult()
    }
}
