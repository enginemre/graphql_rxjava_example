package com.engin.graphqlex.views.userhome

import com.engin.graphqlex.mvibase.IAction

sealed class UserHomeAction:IAction {
    object LoadListAction:UserHomeAction()
    object EmptyAction:UserHomeAction()
}