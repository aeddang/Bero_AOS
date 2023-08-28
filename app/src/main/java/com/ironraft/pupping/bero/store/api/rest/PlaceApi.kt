package com.ironraft.pupping.bero.store.api.rest

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import com.skeleton.theme.ColorApp
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

enum class PlaceCategory {
    Cafe, Vet, Park, None;
    @get:DrawableRes
    val icon:Int
        get() = when(this) {
            Cafe -> R.drawable.pin_cafe
            Vet -> R.drawable.pin_vet
            Park -> R.drawable.pin_park
            else -> R.drawable.pin_park
        }

    @get:DrawableRes
    val iconMark:Int
        get() = when(this) {
            Cafe -> R.drawable.pin_cafe_mark
            Vet -> R.drawable.pin_vet_mark
            Park -> R.drawable.pin_park_mark
            else -> R.drawable.pin_park_mark
        }


    val color:Color
        get() = when(this) {
            Cafe -> ColorApp.gold600
            Vet -> ColorApp.blue600
            Park -> ColorApp.green600
            else -> ColorApp.green600
        }

    @get:StringRes
    val title:Int?
        get() = when(this) {
            Cafe -> R.string.sort_cafe
            Vet -> R.string.sort_cafe
            Park -> R.string.sort_park
            else -> null
        }


    companion object {
        fun getType(value: Int?): PlaceCategory? {
            return when (value) {
                1 -> Cafe
                2 -> Park
                3 -> Vet
                else -> Park
            }
        }
    }
}

interface PlaceApi {

    @GET(Api.Place.search)
    suspend fun getSearch(
        @Query(ApiField.lat) lat: String?,
        @Query(ApiField.lng) lng: String?,
        @Query(ApiField.radius) radius: String?,
        @Query(ApiField.searchType) searchType: String?,
        @Query(ApiField.placeType) placeType: String?,
        @Query(ApiField.zipCode) zipCode: String?
    ): ApiResponse<PlaceData>?

    @GET(Api.Place.visitors)
    suspend fun getVisitors(
        @Path(Api.CONTENT_ID) contentID: String,
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<UserAndPet>?

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST(Api.Place.visit)
    suspend fun postVisitor(
        @Body params: Map<String, @JvmSuppressWildcards Any>
    ): ApiResponse<Any>?
}