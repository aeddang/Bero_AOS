package com.ironraft.pupping.bero.store.api.rest

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import retrofit2.http.*

enum class CodeCategory{
    Breed, Status, Personality, Height, Interest;
    val apiCoreKey:String
        get() = "Status" + this.name.lowercase()
}
enum class ReportType {
    Mission, User, Post, Chat;
    val apiCoreKey : String
        get() = when (this) {
            Mission -> "MISSION"
            User -> "USER"
            Post -> "POST"
            Chat -> "CHAT"
        }
    @get:StringRes
    val completeMessage : Int
        get() = when (this) {
            Post -> R.string.alert_accuseAlbumCompleted
            else -> R.string.alert_accuseUserCompleted
        }
}

enum class AlarmType {
    User, Album, Friend, Chat;
    companion object {
        fun getType(value:String?) : AlarmType?{
            return when (value) {
                "User" -> User
                "Album" -> Album
                "Friend" -> Friend
                "Chat" -> Chat
                else -> null
            }
        }
    }
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

    @POST(Api.Misc.report)
    suspend fun report(
        @Body params: Map<String, String>
    ): ApiResponse<Any>?

    @GET(Api.Misc.alarms)
    suspend fun getAlarms(
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<AlarmData>?

    @GET(Api.Misc.banners)
    suspend fun getBanners(
        @Path(Api.CONTENT_ID) contentID: String,
        @Query(ApiField.exposedDate) exposedDate: String? = ""
    ): ApiResponse<BannerData>?
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

data class AlarmData (
    @SerializedName("alarmType") var alarmType: String? = null,
    @SerializedName("user") var user: UserData? = null,
    @SerializedName("pet") var pet: PetData? = null,
    @SerializedName("album") var album: PictureData? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("contents") var contents: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null
)
data class BannerData (
    @SerializedName("id") var id: String? = null,
    @SerializedName("url") var url: String? = null
)
