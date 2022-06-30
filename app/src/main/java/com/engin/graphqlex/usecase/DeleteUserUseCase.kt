package com.engin.graphqlex.usecase

import com.apollographql.apollo3.api.ApolloResponse
import com.engin.graphqlex.DeleteUserMutation
import com.engin.graphqlex.data.remote.repositorty.UserRemoteRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRemoteRepository: UserRemoteRepository
) : (String)-> Observable<ApolloResponse<DeleteUserMutation.Data>> {
    override fun invoke(p1: String): Observable<ApolloResponse<DeleteUserMutation.Data>> {
        return userRemoteRepository.deleteUser(p1).toObservable()
    }
}