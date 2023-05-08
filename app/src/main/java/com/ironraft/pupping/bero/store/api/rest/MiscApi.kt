package com.ironraft.pupping.bero.store.api.rest

import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import retrofit2.http.*

enum class CodeCategory{
    Breed, Status, Personality, Height, Interest;
    val apiCoreKey:String
        get() = "Status" + this.name.lowercase()
}
interface MiscApi {
    @GET(Api.Misc.codes)
    suspend fun getCodes(
        @Query(ApiField.category) category: String?,
        @Query(ApiField.searchText) searchText: String?
    ): ApiResponse<CodeData>?

    @GET(Api.Misc.weather)
    suspend fun getWeather(
        @Query(ApiField.lat) lat: String?,
        @Query(ApiField.lng) lng: String?
    ): ApiResponse<WeatherData>?
}

data class WeatherData(
    @SerializedName("cityName") var cityName: String? = null,
    @SerializedName("temp") var temp: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("iconId") var iconId: String? = null
)

data class CodeData(
    @SerializedName("category") var category: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("value") var value: String? = null
)