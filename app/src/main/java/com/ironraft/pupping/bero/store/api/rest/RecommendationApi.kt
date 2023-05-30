package com.ironraft.pupping.bero.store.api.rest

import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiResponse
import retrofit2.http.*

interface RecommendationApi {
    @GET(Api.Recommendation.friends)
    suspend fun getFriends(
    ): ApiResponse<UserAndPet>?

}
