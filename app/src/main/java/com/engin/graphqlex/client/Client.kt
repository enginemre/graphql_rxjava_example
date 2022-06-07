package com.engin.graphqlex.client

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


/**
 * Singleton class for creating API client
 */
object Client {

    const val BASE_URL:String = "http://api.spacex.land/graphql/"

    fun getApolloClient() : ApolloClient{

        val okHttpClient : OkHttpClient by lazy{
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                .callTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).
            build()
        }
        return ApolloClient.Builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build()
    }
}