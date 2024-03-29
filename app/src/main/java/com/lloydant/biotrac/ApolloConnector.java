package com.lloydant.biotrac;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApolloConnector {

    private static  final String BASE_URL =  "https://unizik-attendance.netlify.com/.netlify/functions/main";

    private static  final String BASE_URL_UPLOAD =  "https://unizik-attendance.netlify.com/.netlify/functions/upload-attendance-func";


    public static ApolloClient setupApollo(String authHeader) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder().method(original.method(), original.body());
            builder.header("authorization", "Bearer " + authHeader);
            return chain.proceed(builder.build());
        }).build();
        return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build();
    }


    public static ApolloClient setupApolloFileUpload(String authHeader) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder().method(original.method(), original.body());
            builder.header("authorization", "Bearer " + authHeader);
            return chain.proceed(builder.build());
        }).build();
        return ApolloClient.builder().serverUrl(BASE_URL_UPLOAD).okHttpClient(okHttpClient).build();
    }


}
