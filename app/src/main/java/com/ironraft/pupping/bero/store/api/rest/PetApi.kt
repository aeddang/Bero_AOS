package com.ironraft.pupping.bero.store.api.rest

import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PetApi {
    @GET(Api.Pet.Pet)
    suspend fun get(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<PetData>?

    @GET(Api.Pet.Pets)
    suspend fun getUserPets(
        @Query(ApiField.userId) userId: String?
    ): ApiResponse<PetData>?

    @Multipart
    @POST(Api.Pet.Pets)
    suspend fun post(
        @Query(ApiField.userId) userId: String?,
        @Part(ApiField.name) name: RequestBody?,
        @Part(ApiField.breed) breed: RequestBody?,
        @Part(ApiField.birthdate) birthdate: RequestBody?,
        @Part(ApiField.sex) sex: RequestBody?,
        @Part(ApiField.regNumber) regNumber: RequestBody?,
        @Part(ApiField.animalId) animalId: RequestBody?,
        @Part(ApiField.isNeutralized) isNeutralized: RequestBody?,
        @Part(ApiField.isRepresentative) isRepresentative: RequestBody?,
        @Part(ApiField.level) level: RequestBody?,

        @Part(ApiField.tagBreed) tagBreed: RequestBody?,
        @Part(ApiField.tagStatus) tagStatus: RequestBody?,
        @Part(ApiField.tagPersonality) tagPersonality: RequestBody?,

        @Part contents: MultipartBody.Part?
    ): ApiResponse<PetData?>?

    @Multipart
    @PUT(Api.Pet.Pet)
    suspend fun put(
        @Path(Api.CONTENT_ID) contentID: String,
        @Part(ApiField.name) name: RequestBody? = null,
        @Part(ApiField.breed) breed: RequestBody? = null,
        @Part(ApiField.birthdate) birthdate: RequestBody? = null,
        @Part(ApiField.sex) sex: RequestBody? = null,
        @Part(ApiField.regNumber) regNumber: RequestBody? = null,
        @Part(ApiField.animalId) animalId: RequestBody? = null,
        @Part(ApiField.introduction) introduction: RequestBody? = null,
        @Part(ApiField.weight) weight: RequestBody? = null,
        @Part(ApiField.size) size: RequestBody? = null,
        @Part(ApiField.isNeutralized) isNeutralized: RequestBody? = null,
        @Part(ApiField.isRepresentative) isRepresentative: RequestBody? = null,
        @Part(ApiField.tagBreed) tagBreed: RequestBody? = null,
        @Part(ApiField.tagStatus) tagStatus: RequestBody? = null,
        @Part(ApiField.tagPersonality) tagPersonality: RequestBody? = null,
        @Part contents: MultipartBody.Part? = null
    ): ApiResponse<Any?>?

    @DELETE(Api.Pet.Pet)
    suspend fun delete(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<Any?>?
}
data class PetData (
    @SerializedName("petId") var petId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("pictureUrl") var pictureUrl: String? = null,
    @SerializedName("birthdate") var birthdate: String? = null,
    @SerializedName("sex") var sex: String? = null,
    @SerializedName("regNumber") var regNumber: String? = null,
    @SerializedName("animalId") var animalId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("exerciseDistance") var exerciseDistance: Double? = null,
    @SerializedName("exerciseDuration") var exerciseDuration: Double? = null,

    @SerializedName("weight") var weight: Double? = null,
    @SerializedName("size") var size: Double? = null,
    @SerializedName("walkCompleteCnt") var walkCompleteCnt: Int? = null,
    @SerializedName("thumbsupCount") var thumbsupCount: Int? = null,
    @SerializedName("isChecked") var isChecked: Boolean? = null,
    @SerializedName("tagStatus") var tagStatus: String? =null,
    @SerializedName("tagPersonality") var tagPersonality: String? = null,
    @SerializedName("tagHeight") var tagHeight: String? = null,
    @SerializedName("tagInterest") var tagInterest: String? = null,
    @SerializedName("tagBreed") var tagBreed: String? = null,
    @SerializedName("introduce") var introduce: String? = null,
    @SerializedName("userId") var userId:String? = null,
    @SerializedName("isRepresentative") var isRepresentative:Boolean? = null,
    @SerializedName("isNeutered") var isNeutered:Boolean? = null
)
