package com.pixie.android


import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


private var instance: ApolloClient? = null

fun apolloClient(authToken:Double?): ApolloClient {

    if (instance != null) {
        return instance!!
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(authToken))
        .build()

    instance = ApolloClient.builder()
        .serverUrl("https://pixie-server.herokuapp.com/graphql")
        .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory("wss://pixie-server.herokuapp.com/graphql", okHttpClient))
        .okHttpClient(okHttpClient)
        .build()

    return instance!!
}

private class AuthorizationInterceptor(val authToken: Double?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
         val request:Request
        if(authToken==null){
             request = chain.request().newBuilder()
                .build()
        }else {
             request = chain.request().newBuilder()
                .addHeader("authToken", authToken.toString())
                .build()
        }
        return chain.proceed(request)
    }
}
