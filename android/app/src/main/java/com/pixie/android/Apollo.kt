package com.pixie.android

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.builder()
    .serverUrl("https://pixie-server.herokuapp.com/graphql")
    .build()
