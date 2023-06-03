package com.ironraft.pupping.bero.store.api.rest

import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import okhttp3.MultipartBody
import okhttp3.RequestBody


import retrofit2.http.*
enum class WalkStatus {
    Walking, Finish
}
data class WalkadditionalData(
    val loc:LatLng,
    val status:WalkStatus,
    var img:Bitmap? = null,
    var walkTime:Double? = null,
    var walkDistance:Double? = null
)
interface WalkApi {
    @GET(Api.Walk.walk)
    suspend fun get(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<WalkData>?

    @GET(Api.Walk.walks)
    suspend fun getWalks(
        @Query(ApiField.userId) userId: String? = null,
        @Query(ApiField.date) date: String? = null,
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<WalkData>?

    @GET(Api.Walk.searchWalks)
    suspend fun search(
        @Query(ApiField.lat) lat: String? = null,
        @Query(ApiField.lng) lng: String? = null,
        @Query(ApiField.radius) radius:String? = "0",
        @Query(ApiField.latestWalkMin) latestWalkMin:String? = "0",
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<WalkUserData>?

    @GET(Api.Walk.searchWalkFriends)
    suspend fun searchFriends(
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<WalkUserData>?

    @POST(Api.Walk.walk)
    suspend fun post(
        @Body params: Map<String, Any>
    ): ApiResponse<WalkRegistData>?

    @Multipart
    @PUT(Api.Walk.walk)
    suspend fun put(
        @Path(Api.CONTENT_ID) contentID: String,
        @Part("lat") lat: RequestBody? = null,
        @Part("lng") lng: RequestBody? = null,
        @Part("status") status: RequestBody? = null,
        @Part("duration") duration: RequestBody? = null,
        @Part("distance") distance: RequestBody? = null,
        @Part smallContents: MultipartBody.Part?,
        @Part contents: MultipartBody.Part?
    ): ApiResponse<Any>?

    @GET(Api.Walk.route)
    suspend fun getRoute(
        @Query(ApiField.originLat) originLat: String? = null,
        @Query(ApiField.originLng) originLng: String? = null,
        @Query(ApiField.destLat) destLat: Int? = 0,
        @Query(ApiField.destLng) destLng: Int? = 0
    ): ApiResponse<WalkRoute>?

    @GET(Api.Walk.monthlyWalks)
    suspend fun getMonthlyWalks(
        @Query(ApiField.userId) userId: String? = null,
        @Query(ApiField.month) month: String? = null
    ): ApiResponse<String>?

    @GET(Api.Walk.walkSummary)
    suspend fun getWalkSummary(
        @Query(ApiField.petId) petId: String? = null
    ): ApiResponse<WalkSummary>?

}
data class WalkData(
    @SerializedName("walkId") var walkId: Int? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("duration") var duration: Double? = null,
    @SerializedName("distance") var distance: Double? = null,
    @SerializedName("point") var point: Int? = null,
    @SerializedName("exp") var exp: Double? = null,
    @SerializedName("user") var user: UserData? = null,
    @SerializedName("geos") var geos: ArrayList<GeoData>? = null,
    @SerializedName("pets") var pets: ArrayList<PetData>? = null,
    @SerializedName("locations") var locations: ArrayList<WalkLocationData>? = null
)

data class WalkUserData(
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("walkId") var walkId: Int? = null,
    @SerializedName("level") var level: Int? = null,
    @SerializedName("isFriend") var isFriend: Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("location") var location: GeoData? = null,
    @SerializedName("pet") var pet:PetData? = null
    
    
)

data class WalkRegistData(
    @SerializedName("walkId") var walkId: Int? = null,
)

data class WalkLocationData(
    @SerializedName("lat") var lat:Double? = null,
    @SerializedName("lng") var lng:Double? = null,
    @SerializedName("pictureId") var pictureId: Int? = null,
    @SerializedName("pictureUrl") var pictureUrl: String? = null,
    @SerializedName("smallPictureUrl") var smallPictureUrl: String? = null,
    @SerializedName("isExpose") var isExpose:Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null
)

data class WalkSummary(
    @SerializedName("totalDuration") var totalDuration: Double? = null,
    @SerializedName("totalDistance") var totalDistance: Double? = null,
    @SerializedName("totalCount") var totalCount: Double? = null,
    @SerializedName("weeklyReport") var weeklyReport: WalkReport? = null,
    @SerializedName("monthlyReport") var monthlyReport: WalkReport? = null
)

data class WalkReport(
    @SerializedName("duration") var duration: Double? = null,
    @SerializedName("distance") var distance: Double? = null,
    @SerializedName("totalCount") var totalCount: Double? = null,
    @SerializedName("avgCount") var avgCount: Double? = null,
    @SerializedName("times") var times: ArrayList<WalkTime>? = null
)

data class WalkTime(
    @SerializedName("d") var d: String? = null,
    @SerializedName("v") var v: Double? = null
)

data class WalkRoute(
    @SerializedName("legs") var legs: ArrayList<RouteLeg>? = null
)

data class RouteLeg(
    @SerializedName("arrival_time") var arrival_time: Routeinfo? = null,
    @SerializedName("departure_time") var departure_time: Routeinfo? = null,
    @SerializedName("distance") var distance: Routeinfo? = null,
    @SerializedName("duration") var duration: Routeinfo? = null,
    @SerializedName("end_location") var end_location: GeoData? = null,
    @SerializedName("start_location") var start_location: GeoData? = null,
    @SerializedName("steps") var steps: ArrayList<RouteStep>? = null,
    @SerializedName("start_address") var start_address: String? = null,
    @SerializedName("end_address") var end_address: String? = null
)

data class RouteStep(
    @SerializedName("distance") var distance: Routeinfo? = null,
    @SerializedName("duration") var duration: Routeinfo? = null,
    @SerializedName("end_location") var end_location: GeoData? = null,
    @SerializedName("start_location") var start_location: GeoData? = null,
    @SerializedName("polyline") var polyline: Polyline? = null,
    @SerializedName("html_instructions") var html_instructions: String? = null,
)

data class Routeinfo(
    @SerializedName("text") var text: String? = null,
    @SerializedName("value") var value: Double? = null
)
data class Polyline(
    @SerializedName("points") var points: String? = null
)

data class GeometryData(
    @SerializedName("location") var location: GeoData? = null,
    @SerializedName("viewport") var viewport: ViewPortData? = null
)


