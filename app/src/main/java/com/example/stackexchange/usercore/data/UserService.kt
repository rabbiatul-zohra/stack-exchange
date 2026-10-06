package com.example.stackexchange.usercore.data

import com.example.stackexchange.usercore.model.SearchUserListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("2.3/users")
    suspend fun getUsers(
        @Query("inname") name: String,
        @Query("site") site: String = "stackoverflow",
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "asc",
        @Query("sort") sort: String = "name"
    ): SearchUserListResponse

    @GET("2.3/users/{id}")
    suspend fun getUserById(
        @Path("id") userId: Int,
        @Query("site") site: String = "stackoverflow"
    ): SearchUserListResponse
}