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

data class UserData(
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("refUserId")  var refUserId: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("pictureUrl") var pictureUrl: String? = null,
    @SerializedName("providerType") var providerType: String? = null,
    @SerializedName("roleType") var roleType: String? = null,
    @SerializedName("point") var point: Int? = null,
    @SerializedName("birthdate") var birthdate: String? = null,
    @SerializedName("sex") var sex: String? = null,
    @SerializedName("introduce") var introduce: String? = null,
    @SerializedName("exp") var exp:Double? = null,
    @SerializedName("exerciseDistance") var exerciseDistance: Double? = null,
    @SerializedName("exerciseDuration") var exerciseDuration: Double? = null,
    @SerializedName("walkCompleteCnt") var walkCompleteCnt: Int? = null,
    @SerializedName("level") var level: Int? = null,
    @SerializedName("isChecked") var isChecked: Boolean? = null,
    @SerializedName("isFriend") var isFriend: Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("nextLevelExp") var nextLevelExp: Double? = null,
    @SerializedName("prevLevelExp") var prevLevelExp: Double? = null
)
