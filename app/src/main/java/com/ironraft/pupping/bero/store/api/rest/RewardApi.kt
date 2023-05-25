package com.ironraft.pupping.bero.store.api.rest

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import retrofit2.http.*

enum class RewardType {
    RegistPet, Walk, Mission, ParkMission , VisitPlace, RequestFriend;
    @get:DrawableRes
    val icon : Int
        get() = when(this) {
            RegistPet -> R.drawable.add
            Walk -> R.drawable.paw
            Mission, ParkMission -> R.drawable.goal
            VisitPlace -> R.drawable.arrived
            RequestFriend -> R.drawable.add_friend
        }

    val text : String
        get() = when(this) {
            RegistPet -> "Updated first dog profile"
            Walk -> "Completed a walk"
            Mission, ParkMission -> "Completed a mission"
            VisitPlace -> "Left a mark"
            RequestFriend -> "Request a friend"
        }


    companion object {
        fun getType(value:String?) : RewardType? {
            return when (value){
                "REGISTER_PET" -> RewardType.RegistPet
                "WALK" -> RewardType.Walk
                "MISSION" -> RewardType.Mission
                "PARK_MISSION" -> RewardType.ParkMission
                "VISIT_PLACE" -> RewardType.VisitPlace
                "REQUEST_FRIEND" -> RewardType.RequestFriend
                else -> null
            }
        }
    }
}
enum class RewardValueType {
    Exp, Point
}


interface RewardApi {

    @GET(Api.Reward.rewardsHistory)
    suspend fun getHistorys(
        @Query(ApiField.userId) userId: String?,
        @Query(ApiField.page) page:Int? = 0,
        @Query(ApiField.size) size:Int? = ApiValue.PAGE_SIZE,
        @Query(ApiField.rewardType) rewardType:String?
    ): ApiResponse<RewardHistoryData>?


}

data class RewardHistoryData (
    @SerializedName("expType") var expType: String? = null,
    @SerializedName("exp") var exp: Double? = null,
    @SerializedName("pointType") var pointType: String? = null,
    @SerializedName("point") var point: Double? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("userId") var userId: String? = null
)

