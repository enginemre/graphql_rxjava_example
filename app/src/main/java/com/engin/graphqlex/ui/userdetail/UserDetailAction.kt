package com.engin.graphqlex.ui.userdetail

import com.engin.graphqlex.base.IAction



sealed class UserDetailAction : IAction {
    data class LoadUserAction(val userId:String): UserDetailAction()
    data class DeleteUserAction(val userId:String): UserDetailAction()
}
