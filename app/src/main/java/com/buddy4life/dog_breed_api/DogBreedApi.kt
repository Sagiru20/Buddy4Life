package com.buddy4life.dog_breed_api

import com.buddy4life.model.Breed
import retrofit2.Response
import retrofit2.http.GET

interface DogBreedApi {
    @GET("/")
    suspend fun getBreeds(): Response<List<Breed>>
}