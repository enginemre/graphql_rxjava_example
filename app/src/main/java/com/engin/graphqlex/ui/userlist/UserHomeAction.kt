package com.engin.graphqlex.ui.userlist

import com.engin.graphqlex.base.IAction

sealed class UserHomeAction: IAction {
    object LoadListAction: UserHomeAction()
    object EmptyAction: UserHomeAction()
}