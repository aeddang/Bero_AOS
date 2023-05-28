package com.ironraft.pupping.bero.store.api.rest

import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PetApi {
    @GET(Api.Pet.pet)
    suspend fun get(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<PetData>?

    @GET(Api.Pet.pets)
    suspend fun getUserPets(
        @Query(ApiField.userId) userId: String?
    ): ApiResponse<PetData>?

    @Multipart
    @POST(Api.Pet.pets)
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
    ): ApiResponse<PetData>?

    @Multipart
    @PUT(Api.Pet.pet)
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
    ): ApiResponse<Any>?

    @DELETE(Api.Pet.pet)
    suspend fun delete(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<Any>?
}
