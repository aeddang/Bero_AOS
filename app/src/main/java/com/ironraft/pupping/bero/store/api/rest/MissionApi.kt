package com.ironraft.pupping.bero.store.api.rest

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import retrofit2.http.*

enum class MissionCategory {
    Walk, Mission, All;
    val getApiCode : String
        get() = when(this) {
            Walk -> "Walk"
            Mission -> "Mission"
            All -> "All"
        }

    val text : String
        get() = when(this) {
            Walk -> "Walk"
            Mission -> "Mission"
            All -> ""
        }

    @get:DrawableRes
    val icon : Int
        get() = when(this) {
            Walk -> R.drawable.calendar
            Mission -> R.drawable.goal
            All -> R.drawable.paw
        }
    companion object {
        fun getCategory(value:String?) : MissionCategory {
            return when (value){
                "Walk" -> MissionCategory.Walk
                "Mission" -> MissionCategory.Mission
                else -> MissionCategory.All
            }
        }
    }
}

enum class MissionSearchType{
    Distance, Time, Random, User;
    fun getApiCode():String {
        return when (this){
            MissionSearchType.Distance -> "Distance"
            MissionSearchType.Time -> "Time"
            MissionSearchType.Random -> "Random"
            MissionSearchType.User -> "User"
        }
    }
}

interface MissionApi {

    @GET(Api.Mission.missions)
    suspend fun getMissions(
        @Query(ApiField.userId) userId: String?,
        @Query(ApiField.petId) petId: String?,
        @Query(ApiField.missionCategory) missionCategory: String?,
        @Query(ApiField.page) page:Int? = 0,
        @Query(ApiField.size) size:Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<MissionData>?

    @GET(Api.Mission.search)
    suspend fun getSearch(
        @Query(ApiField.searchType) searchType: String?,
        @Query(ApiField.distance) distance: String?,
        @Query(ApiField.lat) lat: String?,
        @Query(ApiField.lng) lng: String?,
        @Query(ApiField.missionCategory) missionCategory: String?,
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<MissionData>?



    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST(Api.Mission.missions)
    suspend fun post(
        @Body params: Map<String, @JvmSuppressWildcards Any>
    ): ApiResponse<MissionData>?

    @GET(Api.Mission.summary)
    suspend fun getSummary(
        @Query(ApiField.petId) petId: String?
    ): ApiResponse<MissionSummary>?
}

data class MissionData (
    @SerializedName("token") var token: String? = null,
    @SerializedName("missionId") var missionId: Int? = null,
    @SerializedName("missionCategory") var missionCategory: String? = null,
    @SerializedName("missionType") var missionType: String? = null,
    @SerializedName("difficulty") var difficulty: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("pictureUrl") var pictureUrl: String? = null,
    @SerializedName("duration") var duration: Double? = null,
    @SerializedName("distance") var distance: Double? = null,
    @SerializedName("point") var point: Int? = null,
    @SerializedName("exp") var exp: Double? = null,
    @SerializedName("user") var user: UserData? = null,
    @SerializedName("geos") var geos: List<GeoData>? = null,
    @SerializedName("pets") var pets: List<PetData>? = null,
    @SerializedName("place") var place:MissionPlace? = null
)
data class MissionPlace (
    @SerializedName("geometry") var geometry: GeometryData? = null,
    @SerializedName("icon") var icon: String? = null,
    @SerializedName("icon_background_color") var icon_background_color: String? = null,
    @SerializedName("name") var name: String? = null,
    //private(set) var photos: String? = nil
    @SerializedName("place_id")  var place_id: String? = null,
    @SerializedName("scope") var scope: String? = null,
    @SerializedName("types") var types: List<String>? = null,
    @SerializedName("vicinity") var vicinity: String? = null
)

data class MissionSummary (
    @SerializedName("totalDuration") var totalDuration: Double? = null,
    @SerializedName("totalDistance") var totalDistance: Double? = null,
    @SerializedName("weeklyReport") var weeklyReport: MissionReport? = null,
    @SerializedName("monthlyReport") var monthlyReport: MissionReport? = null
)

data class MissionReport (
    @SerializedName("totalMissionCount") var totalMissionCount: Double? = null,
    @SerializedName("avgMissionCount") var avgMissionCount: Double? = null,
    @SerializedName("missionTimes") var missionTimes: List<MissionTime>? = null
)

data class MissionTime (
    @SerializedName("d") var d: String? = null,
    @SerializedName("v") var v: Double? = null
)