package com.engin.graphqlex.views.userhome

import com.engin.graphqlex.mvibase.IIntent

sealed class UserHomeIntent:IIntent {
    object LoadListIntent:UserHomeIntent()
    object EmptyIntent:UserHomeIntent()
}