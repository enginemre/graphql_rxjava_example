package com.engin.graphqlex.ui.userlist

import com.engin.graphqlex.base.IIntent

sealed class UserHomeIntent: IIntent {
    object LoadListIntent: UserHomeIntent()
    object EmptyIntent: UserHomeIntent()
}