package com.engin.graphqlex.usecase

import com.apollographql.apollo3.api.ApolloResponse
import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.data.remote.repositorty.UserRemoteRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class UserHomeUseCase @Inject constructor(
    private val userRemoteRepository: UserRemoteRepository
) : ()->Observable<ApolloResponse<UsersListQuery.Data>> {
    override fun invoke(): Observable<ApolloResponse<UsersListQuery.Data>> {
        return userRemoteRepository.getUserList().toObservable()
    }
}