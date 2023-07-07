package com.ironraft.pupping.bero.store.api

import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import retrofit2.http.Query


object Api {
    private const val VERSION_V1 = "v1"
    const val CONTENT_ID = "contentID"

    /**
     * API PATH
     */
    object Auth {
        private const val PATH = "auth"
        const val login = "$VERSION_V1/$PATH/login"
    }

    object User {
        private const val PATH = "users"
        const val user = "$VERSION_V1/$PATH/{${CONTENT_ID}}"
        const val userRegistPushToken = "$VERSION_V1/$PATH/pushToken"
        const val userDelete = "$VERSION_V1/$PATH"
        const val usersBlock = "$VERSION_V1/$PATH/block/{${CONTENT_ID}}"
        const val usersBlockLists = "$VERSION_V1/$PATH/block/list"
    }

    object Pet {
        private const val PATH = "pets"
        const val pets = "$VERSION_V1/$PATH"
        const val pet = "$VERSION_V1/$PATH/{${CONTENT_ID}}"
    }

    object Misc {
        private const val PATH = "misc"
        const val weather = "$VERSION_V1/$PATH/weather"
        const val codes = "$VERSION_V1/$PATH/codes"
        const val report = "$VERSION_V1/$PATH/report/things"
        const val alarms = "$VERSION_V1/$PATH/alarms"
        const val banners = "$VERSION_V1/$PATH/banners/{${CONTENT_ID}}"
    }

    object Mission {
        private const val PATH = "missions"
        const val missions = "$VERSION_V1/$PATH"
        const val search = "$VERSION_V1/$PATH/search"
        const val summary = "$VERSION_V1/$PATH/summary"
    }

    object Place {
        private const val PATH = "place"
        const val place = "$VERSION_V1/$PATH/{${CONTENT_ID}}"
        const val search = "$VERSION_V1/$PATH/search"
        const val visit = "$VERSION_V1/$PATH/visit"
        const val visitors = "$place/visitors"

    }

    object Album {
        private const val PATH = "album"
        const val pictures = "$VERSION_V1/$PATH/pictures"
        const val picturesExplorer = "$VERSION_V1/$PATH/pictures/explorer"
        const val picturesThumbsup = "${Album.pictures}/thumbsup"
    }

    object Vision {
        private const val PATH = "vision"
        const val detect = "$VERSION_V1/$PATH/images/detecthumanwithdog"
    }

    object Friend {
        private const val PATH = "friends"
        const val friend = "$VERSION_V1/${Friend.PATH}/{${CONTENT_ID}}"
        const val friends = "$VERSION_V1/${Friend.PATH}"
        const val friendsIsRequested = "${friends}/isRequested"
        const val friendsRequesting = "${friends}/requesting"
        const val friendsRequest = "${friends}/request"
        const val friendsAccept = "${friends}/accept"
        const val friendsReject = "${friends}/reject"
    }

    object Reward {
        private const val PATH = "rewards"
        const val rewards = "$VERSION_V1/${PATH}"
        const val rewardsHistory = "$VERSION_V1/${PATH}/histories"
    }

    object Walk {
        private const val PATH = "walk"
        const val walks = "$VERSION_V1/${PATH}"
        const val walk = "${walks}/{${CONTENT_ID}}"
        const val searchWalks = "${walks}/search"
        const val searchWalkFriends = "${searchWalks}/friends"
        const val monthlyWalks = "${walks}/monthlyList"

        const val walkSummary = "${walks}/summary"
        const val route = "${walks}/directions"
    }

    object Chat {
        private const val PATH = "chats"
        const val chats = "$VERSION_V1/${PATH}"
        const val chat = "${chats}/{${CONTENT_ID}}"
        const val chatSend = "${chats}/send"
        const val chatRooms = "${chats}/rooms"
        const val chatRoom = "${chatRooms}/{${CONTENT_ID}}"
        const val chatRoomList = "${chatRooms}/{${CONTENT_ID}}/list"
        const val chatRoomRead = "${chatRooms}/{${CONTENT_ID}}/read"
    }
    object Recommendation {
        private const val PATH = "recommendation"
        private const val recommendation = "$VERSION_V1/${PATH}"
        const val friends = "${recommendation}/friends"
    }


}



object ApiValue{
    const val PAGE_SIZE = 20
}


object ApiCode{
    const val  invalidToken = "C001"
    const val  notFound = "C005" // 결과값이 없습니다 ** 정상으로 받은결과값 data가 null일때
}

object ApiField {
    const val lat = "lat"
    const val lng = "lng"
    const val email = "email"
    const val distance = "distance"
    const val userId = "userId"
    const val petId ="petId"
    const val randId ="randId"
    const val searchType = "searchType"
    const val missionCategory = "missionCategory"
    const val page = "page"
    const val size = "size"
    const val name = "name"
    const val breed = "breed"
    const val birthdate = "birthdate"
    const val sex = "sex"
    const val regNumber = "regNumber"
    const val animalId = "animalId"
    const val introduction = "introduction"
    const val level = "level"
    const val weight = "weight"
    const val status = "status"
    const val pictureType = "pictureType"
    const val ownerId = "ownerId"
    const val pictureIds = "pictureIds"
    const val isNeutralized = "isNeutralized"
    const val isRepresentative = "isRepresentative"
    const val tagBreed = "tagBreed"
    const val tagStatus = "tagStatus"
    const val tagPersonality = "tagPersonality"
    const val category = "category"
    const val searchText = "searchText"
    const val otherUserId = "otherUserId"
    const val isExpose = "isExpose"
    const val referenceId = "referenceId"
    const val rewardType = "rewardType"
    const val radius = "radius"
    const val placeType = "placeType"
    const val zipCode = "zipCode"
    const val date = "date"
    const val latestWalkMin = "latestWalkMin"
    const val originLat = "originLat"
    const val originLng = "originLng"
    const val destLat = "destLat"
    const val destLng = "destLng"
    const val month = "month"
    const val otherUser = "otherUser"
    const val receiver = "receiver"
    const val title = "title"
    const val contents = "contents"
    const val googlePlaceId = "googlePlaceId"
    const val exposedDate = "exposedDate"
}


enum class ApiType{
    AuthLogin, AuthReflash , GetUser, UpdateUser, RegistPush, DeleteUser,
    GetWeather, GetCode, GetBanner,
    GetMission, SearchMission, CompleteMission, GetMissionSummary,
    GetPet, GetPets, RegistPet, UpdatePetImage, UpdatePet, DeletePet,  ChangeRepresentativePet,
    GetAlbumPictures, GetExplorePictures, RegistAlbumPicture, DeleteAlbumPictures, UpdateAlbumPicturesLike, UpdateAlbumPicturesExpose,
    CheckHumanWithDog,
    GetFriends, GetRequestFriends, GetRequestedFriends, CheckRequestFriends,
    RequestFriend, DeleteFriend, RejectFriend, AcceptFriend,
    GetBlockUsers, RequestBlock,
    PostReport, Report,
    GetAlarms,
    GetRewardHistory,
    GetPlace, GetPlaceVisitors, RegistVisitor,
    GetWalk,GetWalks, GetUserWalks,
    SearchWalk, SearchWalkFriends, SearchLatestWalk,
    RegistWalk, UpdateWalk, CompleteWalk,
    GetWalkSummary, GetMonthlyWalk,
    GetChats, GetRoomChats, DeleteChat, DeleteAllChat, SendChat,
    GetChatRooms, ReadChatRoom, DeleteChatRoom,
    GetRecommandationFriends
    ;



    fun coreDataKey(requestData:Any?) : String? {
        return when (this) {
            GetCode -> {
                val cate = requestData as? CodeCategory
                cate?.apiCoreKey
            }
            else -> null
        }
    }
}