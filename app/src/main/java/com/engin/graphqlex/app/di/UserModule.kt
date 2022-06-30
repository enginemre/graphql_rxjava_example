package com.engin.graphqlex.app.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.engin.graphqlex.app.utils.Credentials
import com.engin.graphqlex.data.remote.Client
import com.engin.graphqlex.data.remote.repositorty.UserRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class UserModule {

    @Singleton
    @Provides
    fun provideApolloClient(okHttpClient:OkHttpClient):ApolloClient{
        return ApolloClient.Builder().serverUrl(Credentials.BASE_URL).okHttpClient(okHttpClient).build()
    }

    @Singleton
    @Provides
    fun provideUserRepository(client: Client) : UserRemoteRepository{
         return UserRemoteRepository(client)
    }
}