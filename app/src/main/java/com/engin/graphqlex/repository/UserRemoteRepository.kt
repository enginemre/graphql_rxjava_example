package com.engin.graphqlex.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.rx3.Rx3Apollo
import com.engin.graphqlex.*
import com.engin.graphqlex.service.Client
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class UserRemoteRepository @Inject constructor(private val client :Client):IUserRemoteRepository {
    /**
     * Getting users list ordered by timestamp
     * @param limit limit of user count for one request
     *  @return [Single] observable object for ui handling etc.
      */
    override
    fun getUserList(limit:Int) :Single<ApolloResponse<UsersListQuery.Data>>{
        val call =  client.getApolloClient().query(UsersListQuery(limit))
        return Rx3Apollo.single(call).subscribeOn(Schedulers.io())
    }

    /**
     * Creating user
     * @param name,rockets,twitter
     *  @return [Single] observable object for ui handling etc.
     */
    override
    fun createUser(name:String,rocket:String,twitter:String): Single<ApolloResponse<InsertUserMutation.Data>> {
        val call = client.getApolloClient().mutation(InsertUserMutation(name,rocket,twitter))
        return Rx3Apollo.single(call).subscribeOn(Schedulers.io())
    }
    /**
     * finding  user by unique id
     * @param id id  of user
     *  @return [Single] observable object for ui handling etc.
     */
    override
    fun getUserById(id:String):Single<ApolloResponse<UserByIdQuery.Data>>{
        val call = client.getApolloClient().query(UserByIdQuery(id))
        return Rx3Apollo.single(call).subscribeOn(Schedulers.io())
    }

    /**
     * updating  user information
     * @param name,rockets,twitter
     * @param id id of user
     *  @return [Single] observable object for ui handling etc.
     */
    override
    fun updateUser(name: String,rocket: String,twitter: String,id:String) : Single<ApolloResponse<UpdateUserMutation.Data>>{
        val call = client.getApolloClient().mutation(UpdateUserMutation(id,name,rocket,twitter))
        return Rx3Apollo.single(call).subscribeOn(Schedulers.io())
    }
    /**
     * deleting  user
     * @param id id of user
     *  @return [Single] observable object for ui handling etc.
     */
    override
    fun deleteUser(id:String): Single<ApolloResponse<DeleteUserMutation.Data>>{
        val call = client.getApolloClient().mutation(DeleteUserMutation(id))
        return Rx3Apollo.single(call).subscribeOn(Schedulers.io())
    }
}