package com.example.viacepintegration.http.service

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("{cep}/json/")
    suspend fun getViaCepResponse(@Path("cep") cep: String): ViaCepResponse
}