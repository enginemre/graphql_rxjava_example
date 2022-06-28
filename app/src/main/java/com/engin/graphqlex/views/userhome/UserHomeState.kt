package com.engin.graphqlex.views.userhome

import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.mvibase.IState

data class UserHomeState(
    val isLoading:Boolean,
    val data:List<UsersListQuery.User>,
    val error: Throwable?
):IState {
    companion object {
        fun idle(): UserHomeState = UserHomeState(false, arrayListOf<UsersListQuery.User>(), null)

    }
}