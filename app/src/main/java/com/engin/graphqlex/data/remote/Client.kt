package com.engin.graphqlex.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.engin.graphqlex.app.utils.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Creating API client
 */
class Client @Inject constructor() {

    fun getApolloClient() : ApolloClient{

        val okHttpClient : OkHttpClient by lazy{
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                .callTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).
            build()
        }
        return ApolloClient.Builder().serverUrl(Credentials.BASE_URL).okHttpClient(okHttpClient).build()
    }
}