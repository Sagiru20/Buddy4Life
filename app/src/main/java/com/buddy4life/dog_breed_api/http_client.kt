package com.buddy4life.dog_breed_api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        const val DOG_BREED_API_HOST = "dogbreeddb.p.rapidapi.com"
        private const val DOG_BREED_API_BASE_URL = "https://dogbreeddb.p.rapidapi.com/"

        class MyInterceptor : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder().addHeader(
                    "X-RapidAPI-Key", "8739f070b1msh521b2189c8925c2p18869ajsn67c9be5b6e3f"
                ).addHeader("X-RapidAPI-Host", DOG_BREED_API_HOST).build()
                return chain.proceed(request)
            }
        }

        private val client = OkHttpClient.Builder().apply {
            addInterceptor(MyInterceptor())
        }.build()

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder().baseUrl(DOG_BREED_API_BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
    }
}