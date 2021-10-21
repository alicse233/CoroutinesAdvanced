package com.example.coroutinesadvanced

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface MyApi {

    @GET()
    fun getListOldWay() : Call<List<Person>>

    @GET()
    suspend fun getListCoroutineWay() : Call<Person>

    @GET()
    suspend fun getListCoroutineUpdatedWay() : Response<Person>
}