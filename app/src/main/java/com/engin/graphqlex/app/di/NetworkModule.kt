package com.engin.graphqlex.app.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.engin.graphqlex.app.utils.Credentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    /**
     * Providing Http Log interceptor
     */
    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return logInterceptor
    }

    /**
     * Providing OkHttp client for requesting
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build()
    }


    /**
     * Providing ApolloClient for GraphQL requests
    @Singleton
    @Provides
    fun provideApolloClient(okHttpClient:OkHttpClient) : ApolloClient {
        return ApolloClient.Builder().serverUrl(Credentials.BASE_URL).okHttpClient(okHttpClient).build()
    }*/
}