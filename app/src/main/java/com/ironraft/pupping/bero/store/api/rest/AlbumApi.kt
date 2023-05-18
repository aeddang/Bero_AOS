package com.ironraft.pupping.bero.store.api.rest

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

enum class AlbumCategory{
    Pet, User;
    val getApiCode : String
        get() = when(this) {
            Pet -> "Pet"
            User -> "User"
        }
    companion object {
        fun getCategory(value :String?) : AlbumCategory?
        {
            return when(value){
                "Pet" -> AlbumCategory.Pet
                "User" -> AlbumCategory.User
                else -> null
            }
        }
    }
}

data class AlbumData(
    val type:AlbumCategory,
    val image:Bitmap?,
    var thumb:Bitmap? = null,
    var isExpose:Boolean = false,
    var referenceId:String? = null
)

interface AlbumApi {
    @GET(Api.Album.pictures)
    suspend fun get(
        @Query(ApiField.ownerId) ownerId: String,
        @Query(ApiField.pictureType) pictureType: String?,
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<PictureData>?

    @Multipart
    @POST(Api.Album.pictures)
    suspend fun post(
        @Part(ApiField.ownerId) ownerId: RequestBody?,
        @Part(ApiField.pictureType) pictureType: RequestBody?,
        @Part(ApiField.userId) userId: RequestBody?,
        @Part(ApiField.isExpose) isExpose: RequestBody?,
        @Part(ApiField.referenceId) referenceId: RequestBody?,
        @Part smallContents: MultipartBody.Part?,
        @Part contents: MultipartBody.Part?
    ): ApiResponse<PictureData?>?

    @Headers("Content-Type: application/json;charset=UTF-8")
    @PUT(Api.Album.pictures)
    @JvmSuppressWildcards
    suspend fun put(
        @Body params: Map<String, Any>
    ): ApiResponse<Any?>?
    @Headers("Content-Type: application/json;charset=UTF-8")
    @PUT(Api.Album.picturesThumbsup)
    @JvmSuppressWildcards
    suspend fun putThumbsup(
        @Body params: Map<String, Any>
    ): ApiResponse<Any?>?

    @DELETE(Api.Album.pictures)
    suspend fun delete(
        @Query(ApiField.pictureIds) pictureIds: String,
    ): ApiResponse<Any?>?
}

data class PictureData (
    @SerializedName("pictureId") var pictureId: Int? = null,
    @SerializedName("pictureType") var pictureType: String? = null,
    @SerializedName("ownerId") var ownerId: String? = null,
    @SerializedName("pictureUrl") var pictureUrl: String? = null,
    @SerializedName("smallPictureUrl") var smallPictureUrl: String? = null,
    @SerializedName("thumbsupCount") var thumbsupCount: Double? = null,
    @SerializedName("isChecked") var isChecked: Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("isExpose") var isExpose: Boolean? = null,
    @SerializedName("referenceId") var referenceId: String? = null
)
