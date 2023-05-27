package com.ironraft.pupping.bero.store.api.rest

import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface UserApi {
    @GET(Api.User.user)
    suspend fun get(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<UserData>?

    @DELETE(Api.User.userDelete)
    suspend fun delete(
    ): ApiResponse<Any?>

    @Multipart
    @PUT(Api.User.user)
    suspend fun put(
        @Path(Api.CONTENT_ID) contentID: String,
        @Part("name") name: RequestBody? = null,
        @Part("birthdate") birthdate: RequestBody? = null,
        @Part("sex") sex: RequestBody? = null,
        @Part("introduce") introduce: RequestBody? = null,
        @Part contents: MultipartBody.Part?
    ): ApiResponse<Any?>?
    @POST(Api.User.userRegistPushToken)
    suspend fun post(
        @Body params: Map<String, String>
    ): ApiResponse<Any?>?


    @GET(Api.User.usersBlockLists)
    suspend fun getBlocks(
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<UserData>?

    @POST(Api.User.usersBlock)
    suspend fun block(
        @Path(Api.CONTENT_ID) contentID: String,
        @Query("isBlock") isBlock: Boolean? = null
    ): ApiResponse<Any>?
}


