package com.pixie.android

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import okhttp3.OkHttpClient


val okHttpClient = OkHttpClient.Builder()
//    .addInterceptor(AuthorizationInterceptor(context))
    .build()

val apolloClient = ApolloClient.builder()
    .serverUrl("https://pixie-server.herokuapp.com/graphql")
    .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory(
        "wss://pixie-server.herokuapp.com/graphql", okHttpClient))
    .build()
