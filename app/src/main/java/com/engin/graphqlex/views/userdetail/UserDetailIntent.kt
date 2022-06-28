package com.engin.graphqlex.views.userdetail

import com.engin.graphqlex.mvibase.IIntent

sealed class UserDetailIntent:IIntent {
    data class LoadUserIntent(val userID:String):UserDetailIntent()
    data class DeleteUserIntent(val userId:String):UserDetailIntent()
}