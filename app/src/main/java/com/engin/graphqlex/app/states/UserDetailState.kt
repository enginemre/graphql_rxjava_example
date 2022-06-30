package com.engin.graphqlex.app.states

import com.engin.graphqlex.DeleteUserMutation
import com.engin.graphqlex.UserByIdQuery
import com.engin.graphqlex.base.IState

data class UserDetailState(
    val isLoading:Boolean,
    val user: UserByIdQuery.Users_by_pk?,
    val deleteData : DeleteUserMutation.Data?,
    val error: Throwable?
): IState {
    companion object {
        fun idle(): UserDetailState = UserDetailState(
            isLoading = false,
            user = null,
            deleteData =null,
            error = null
        )
    }
}