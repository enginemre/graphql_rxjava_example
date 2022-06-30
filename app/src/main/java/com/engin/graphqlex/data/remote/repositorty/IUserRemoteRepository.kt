package com.engin.graphqlex.data.remote.repositorty

import com.apollographql.apollo3.api.ApolloResponse
import com.engin.graphqlex.*
import io.reactivex.rxjava3.core.Single

/**
 * Users operations
 */
interface IUserRemoteRepository {

    fun getUserList(limit:Int = 10) : Single<ApolloResponse<UsersListQuery.Data>>
    fun createUser(name:String,rocket:String,twitter:String): Single<ApolloResponse<InsertUserMutation.Data>>
    fun getUserById(id:String):Single<ApolloResponse<UserByIdQuery.Data>>
    fun updateUser(name: String,rocket: String,twitter: String,id:String) : Single<ApolloResponse<UpdateUserMutation.Data>>
    fun deleteUser(id:String): Single<ApolloResponse<DeleteUserMutation.Data>>

}