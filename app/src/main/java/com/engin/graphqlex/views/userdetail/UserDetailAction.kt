package com.engin.graphqlex.views.userdetail

import com.engin.graphqlex.mvibase.IAction



sealed class UserDetailAction :IAction{
    data class LoadUserAction(val userId:String):UserDetailAction()
    data class DeleteUserAction(val userId:String):UserDetailAction()
}
