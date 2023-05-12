package com.ironraft.pupping.bero.store.api.rest

import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface FriendApi {
    @GET(Api.Friend.friends)
    suspend fun get(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<UserData>?

    @GET(Api.Friend.friends)
    suspend fun getFriends(
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<FriendData>?

    @GET(Api.Friend.friendsIsRequested)
    suspend fun requestedFriends(
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<FriendData>?

    @GET(Api.Friend.friendsRequesting)
    suspend fun requestFriends(
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<FriendData>?

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST(Api.Friend.friendsRequest)
    suspend fun request(
        @Query(ApiField.otherUserId) otherUserId: String? = "0"
    ): ApiResponse<Any?>?

    @DELETE(Api.Friend.friends)
    suspend fun delete(
        @Query(ApiField.otherUserId) otherUserId: String? = "0"
    ): ApiResponse<Any?>?

    @PUT(Api.Friend.friendsAccept)
    suspend fun accept(
        @Query(ApiField.otherUserId) otherUserId: String? = "0"
    ): ApiResponse<Any?>?

    @PUT(Api.Friend.friendsReject)
    suspend fun reject(
        @Query(ApiField.otherUserId) otherUserId: String? = "0"
    ): ApiResponse<Any?>?



}

data class FriendData(
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("refUserId") var refUserId: String? = null,
    @SerializedName("isAccepted") var isAccepted: Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("petId") var petId: Int? = null,
    @SerializedName("userImg") var userImg: String? = null,
    @SerializedName("petImg") var petImg: String? = null,
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("petName") var petName: String? = null,
    @SerializedName("level") var level: Int? = null
)

