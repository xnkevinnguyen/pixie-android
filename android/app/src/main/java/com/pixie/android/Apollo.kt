package com.pixie.android

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.builder()
    .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com")
    .build()
