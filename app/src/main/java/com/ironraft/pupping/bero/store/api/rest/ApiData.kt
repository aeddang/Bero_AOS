package com.ironraft.pupping.bero.store.api.rest

import com.google.gson.annotations.SerializedName

data class UserAndPet(
    @SerializedName("user") var user: UserData? = null,
    @SerializedName("pet") var pet: PetData? = null
)

data class GeoData(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null
)
data class ViewPortData (
    @SerializedName("northeast") var northeast: GeoData? = null,
    @SerializedName("southwest") var southwest: GeoData? = null
)


data class PlaceData (
    @SerializedName("placeId")  var placeId: Int? = null,
    @SerializedName("placeCategory")  var placeCategory: Int? = null,
    @SerializedName("location")  var location: String? = null,
    @SerializedName("name")  var name: String? = null,
    @SerializedName("googlePlaceId")  var googlePlaceId: String? = null,
    @SerializedName("createdAt")  var createdAt: String? = null,
    @SerializedName("visitorCnt")  var visitorCnt:Int? = null,
    @SerializedName("isVisited")  var isVisited:Boolean? = null,
    @SerializedName("visitors")  var visitors: List<UserAndPet>? = null,
    @SerializedName("place")  var place:MissionPlace? = null,
    @SerializedName("point")  var point:Int? = null,
    @SerializedName("exp")  var exp:Double? = null
)

data class UserData(
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("refUserId")  var refUserId: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("pictureUrl") var pictureUrl: String? = null,
    @SerializedName("providerType") var providerType: String? = null,
    @SerializedName("roleType") var roleType: String? = null,
    @SerializedName("point") var point: Int? = null,
    @SerializedName("birthdate") var birthdate: String? = null,
    @SerializedName("sex") var sex: String? = null,
    @SerializedName("introduce") var introduce: String? = null,
    @SerializedName("exp") var exp:Double? = null,
    @SerializedName("exerciseDistance") var exerciseDistance: Double? = null,
    @SerializedName("exerciseDuration") var exerciseDuration: Double? = null,
    @SerializedName("walkCompleteCnt") var walkCompleteCnt: Int? = null,
    @SerializedName("level") var level: Int? = null,
    @SerializedName("isChecked") var isChecked: Boolean? = null,
    @SerializedName("isFriend") var isFriend: Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("nextLevelExp") var nextLevelExp: Double? = null,
    @SerializedName("prevLevelExp") var prevLevelExp: Double? = null
)

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
