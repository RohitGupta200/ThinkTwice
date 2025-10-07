package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.*
import de.jensklingenberg.ktorfit.http.*

interface UserApi {

    @GET("api/v1/users")
    suspend fun getAllUsers(
        @Header("Authorization") authorization: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): UsersResponse

    @GET("api/v1/users/{user_id}")
    suspend fun getUserById(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: Int
    ): UserDto

    @POST("users/")
    suspend fun createUser(
        @Header("Authorization") authorization: String,
        @Body request: CreateUserRequest
    ): UserDto

    @PUT("api/v1/users/{user_id}")
    suspend fun updateUser(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: Int,
        @Body request: UpdateUserRequest
    ): UserDto

    @DELETE("users/{user_id}")
    suspend fun deleteUser(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: Int
    )
}