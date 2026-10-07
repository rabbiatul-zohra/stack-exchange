package com.example.stackexchange.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val SITE_QUERY = "stackoverflow"

interface UserService {
    @GET("users")
    suspend fun getUsers(
        @Query("inname") name: String,
        @Query("site") site: String = SITE_QUERY,
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "asc",
        @Query("sort") sort: String = "name"
    ): SearchUserListResponse

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: Int,
        @Query("site") site: String = SITE_QUERY
    ): SearchUserListResponse

    @GET("users/{id}/top-tags")
    suspend fun getUserTopTags(
        @Path("id") userId: Int,
        @Query("site") site: String = SITE_QUERY
    ): TopTagsListResponse

    @GET("users/{id}/badges")
    suspend fun getUserBadges(
        @Path("id") userId: Int,
        @Query("site") site: String = SITE_QUERY
    ): BadgesListResponse
}