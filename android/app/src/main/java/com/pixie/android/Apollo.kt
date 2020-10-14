package com.pixie.android


import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


private var instance: ApolloClient? = null
private var savedAuthToken:Double? = null
object HeadersProvider {
    val HEADERS = mapOf(
        "authToken" to "1"
    )
}

fun apolloClient(authToken:Double?): ApolloClient {

    if (instance != null && authToken== savedAuthToken) {
        return instance!!
    }
    savedAuthToken=authToken

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(authToken))
        .build()
    val subParams = SubscriptionConnectionParams()
    if (authToken!=null){
        subParams.put("authToken",authToken.toInt())

    }

    instance = ApolloClient.builder()
        .serverUrl("https://pixie-server.herokuapp.com/graphql")
        .subscriptionConnectionParams(subParams)
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
                .addHeader("authToken", authToken.toInt().toString())
                .build()
        }
        return chain.proceed(request)
    }
}
