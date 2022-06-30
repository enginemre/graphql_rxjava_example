package com.engin.graphqlex.ui.userdetail

import com.engin.graphqlex.base.IIntent

sealed class UserDetailIntent: IIntent {
    data class LoadUserIntent(val userID:String): UserDetailIntent()
    data class DeleteUserIntent(val userId:String): UserDetailIntent()
}