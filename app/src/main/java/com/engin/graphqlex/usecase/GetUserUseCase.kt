package com.engin.graphqlex.usecase

import com.apollographql.apollo3.api.ApolloResponse
import com.engin.graphqlex.UserByIdQuery
import com.engin.graphqlex.UsersListQuery
import com.engin.graphqlex.data.remote.repositorty.UserRemoteRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRemoteRepository: UserRemoteRepository
) : (String)-> Observable<ApolloResponse<UserByIdQuery.Data>> {

    override fun invoke(p1: String): Observable<ApolloResponse<UserByIdQuery.Data>> {
        return userRemoteRepository.getUserById(p1).toObservable()
    }
}