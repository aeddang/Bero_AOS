package com.ironraft.pupping.bero.store.api

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import com.lib.util.*
import com.ironraft.pupping.bero.BuildConfig
import com.ironraft.pupping.bero.store.api.rest.*
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.ModifyUserProfileData
import com.skeleton.module.network.NetworkFactory
import com.skeleton.sns.SnsUser
import com.skeleton.theme.DimenApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.time.LocalDate
import java.util.Date
import java.util.HashMap

class ApiBridge(
    private val context: Context,
    networkFactory: NetworkFactory,
    interceptor: ApiInterceptor
) {
    val auth: AuthApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(AuthApi::class.java)
    val user: UserApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(UserApi::class.java)
    private val pet: PetApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(PetApi::class.java)
    private val misc: MiscApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(MiscApi::class.java)
    private val mission: MissionApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(MissionApi::class.java)
    private val album: AlbumApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(AlbumApi::class.java)
    private val vision: VisionApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(VisionApi::class.java)
    private val friend: FriendApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(FriendApi::class.java)
    private val reward: RewardApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(RewardApi::class.java)
    private val place: PlaceApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(PlaceApi::class.java)
    private val walk: WalkApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(WalkApi::class.java)
    private val chat: ChatApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(ChatApi::class.java)
    private val recommendation: RecommendationApi = networkFactory.getRetrofit(BuildConfig.APP_REST_ADDRESS, listOf( interceptor ) )
        .create(RecommendationApi::class.java)

    @Suppress("UNCHECKED_CAST")
    fun getApi(apiQ: ApiQ, snsUser:SnsUser?) = runBlocking {
        return@runBlocking when(apiQ.type){
            ApiType.AuthLogin -> auth.post(apiQ.body as Map<String, String>)
            ApiType.AuthReflash -> auth.reflash(apiQ.body as Map<String, String>)
            ApiType.GetUser -> user.get(apiQ.contentID)
            ApiType.DeleteUser -> user.delete()
            ApiType.UpdateUser -> getUpdateUserProfile( snsUser?.snsID,  apiQ.requestData as? ModifyUserProfileData )
            ApiType.RegistPush -> user.post(apiQ.body as Map<String, String>)
            ApiType.GetWeather -> misc.getWeather(apiQ.query?.get(ApiField.lat), apiQ.query?.get(ApiField.lng))
            ApiType.GetCode -> misc.getCodes(apiQ.query?.get(ApiField.category), apiQ.query?.get(ApiField.searchText))
            ApiType.GetBanner -> misc.getBanners(apiQ.contentID, apiQ.requestData as? String)
            ApiType.GetMission -> mission.getMissions(
                apiQ.query?.get(ApiField.userId), apiQ.query?.get(ApiField.petId), apiQ.query?.get(ApiField.missionCategory),apiQ.page, apiQ.pageSize)
            ApiType.SearchMission -> mission.getSearch(
                apiQ.query?.get(ApiField.searchType), apiQ.query?.get(ApiField.distance),
                apiQ.query?.get(ApiField.lat), apiQ.query?.get(ApiField.lng), apiQ.query?.get(ApiField.missionCategory),apiQ.page, apiQ.pageSize)
            ApiType.CompleteMission -> mission.post(apiQ.body as Map<String, Any>)

            ApiType.GetMissionSummary -> mission.getSummary(apiQ.contentID)
            ApiType.GetPet -> pet.get(apiQ.contentID)
            ApiType.GetPets -> pet.getUserPets(apiQ.contentID)
            ApiType.RegistPet -> getRegistPetProfile(snsUser?.snsID, apiQ.requestData as? ModifyPetProfileData)
            ApiType.UpdatePetImage -> getUpdatePetProfile(apiQ.contentID, img = apiQ.requestData as? Bitmap)
            ApiType.UpdatePet -> getUpdatePetProfile(apiQ.contentID, profile = apiQ.requestData as? ModifyPetProfileData)
            ApiType.ChangeRepresentativePet -> getUpdateRepresentative(apiQ.contentID)
            ApiType.DeletePet -> pet.delete(apiQ.contentID)
            ApiType.GetAlbumPictures -> album.get(apiQ.contentID, apiQ.query?.get(ApiField.pictureType),apiQ.page, apiQ.pageSize)
            ApiType.GetExplorePictures -> album.getExplorer(apiQ.contentID, apiQ.query?.get(ApiField.searchType),apiQ.page, apiQ.pageSize)
            ApiType.RegistAlbumPicture -> getRegistAlbumPicture(apiQ.contentID, snsUser?.snsID, apiQ.requestData as? AlbumData)
            ApiType.UpdateAlbumPicturesLike -> getUpdateLikeAlbumPicture(apiQ.contentID, apiQ.requestData as? Boolean)
            ApiType.UpdateAlbumPicturesExpose -> getUpdateExposeAlbumPicture(apiQ.contentID, apiQ.requestData as? Boolean)
            ApiType.DeleteAlbumPictures -> album.delete(apiQ.query?.get(ApiField.pictureIds) ?: "")
            ApiType.CheckHumanWithDog -> getVisionCheck(apiQ.requestData as? Bitmap)
            ApiType.GetFriends -> friend.getFriends(apiQ.contentID, apiQ.page, apiQ.pageSize)
            ApiType.GetRequestFriends -> friend.requestFriends(apiQ.page, apiQ.pageSize)
            ApiType.CheckRequestFriends -> friend.requestFriends(apiQ.page, apiQ.pageSize)
            ApiType.GetRequestedFriends -> friend.requestedFriends( apiQ.page, apiQ.pageSize)
            ApiType.RequestFriend -> friend.request(apiQ.contentID)
            ApiType.AcceptFriend -> friend.accept(apiQ.contentID)
            ApiType.RejectFriend -> friend.reject(apiQ.contentID)
            ApiType.DeleteFriend -> friend.delete(apiQ.contentID)
            ApiType.GetBlockUsers -> user.getBlocks(apiQ.page, apiQ.pageSize)
            ApiType.RequestBlock -> user.block(apiQ.contentID, apiQ.requestData as? Boolean )
            ApiType.PostReport-> getReport(apiQ.contentID, ReportType.Post, apiQ.requestData as? String)
            ApiType.Report-> getReport(apiQ.contentID, apiQ.requestData as? ReportType ?: ReportType.User)
            ApiType.GetAlarms -> misc.getAlarms(apiQ.page, apiQ.pageSize)
            ApiType.GetRewardHistory -> reward.getHistorys(apiQ.contentID, apiQ.page, apiQ.pageSize, (apiQ.requestData as? RewardValueType)?.name)
            ApiType.GetPlace -> place.getSearch(
                apiQ.query?.get(ApiField.lat),  apiQ.query?.get(ApiField.lng), apiQ.query?.get(ApiField.radius),
                apiQ.query?.get(ApiField.searchType), apiQ.query?.get(ApiField.placeType),apiQ.query?.get(ApiField.zipCode)
            )
            ApiType.GetPlaceVisitors -> place.getVisitors(apiQ.contentID, apiQ.page, apiQ.pageSize)
            ApiType.RegistVisitor -> place.postVisitor(apiQ.body as Map<String, Any>)

            ApiType.GetWalk -> walk.get(apiQ.contentID)
            ApiType.GetWalks -> walk.getWalks(null,
                (apiQ.requestData as? Date)?.toDateFormatter("yyyy-MM-dd"),apiQ.page, apiQ.pageSize)
            ApiType.GetUserWalks -> walk.getWalks(
                apiQ.contentID, null,apiQ.page, apiQ.pageSize)
            ApiType.SearchLatestWalk -> {
                if(apiQ.prevData == null) {
                    walk.search(
                        apiQ.query?.get(ApiField.lat), apiQ.query?.get(ApiField.lng), apiQ.query?.get(ApiField.radius),
                        apiQ.query?.get(ApiField.latestWalkMin),apiQ.page, apiQ.pageSize)
                } else {
                    walk.searchFriends(apiQ.page, apiQ.pageSize)
                }
            }
            ApiType.SearchWalk -> walk.search(
                apiQ.query?.get(ApiField.lat), apiQ.query?.get(ApiField.lng), apiQ.query?.get(ApiField.radius),
                apiQ.query?.get(ApiField.latestWalkMin),apiQ.page, apiQ.pageSize)
            ApiType.SearchWalkFriends -> walk.searchFriends(apiQ.page, apiQ.pageSize)
            ApiType.RegistWalk -> walk.post(apiQ.body as Map<String, Any>)
            ApiType.UpdateWalk -> getUpdateWalk(apiQ.contentID, apiQ.requestData as? WalkadditionalData)
            ApiType.CompleteWalk -> getUpdateWalk(apiQ.contentID, apiQ.requestData as? WalkadditionalData)
            ApiType.GetWalkSummary -> walk.getWalkSummary(apiQ.contentID)
            ApiType.GetMonthlyWalk -> walk.getMonthlyWalks(apiQ.contentID, (apiQ.requestData as? Date)?.toDateFormatter("yyyy-MM"))
            ApiType.GetChats -> chat.get(apiQ.contentID, apiQ.page, apiQ.pageSize)
            ApiType.GetRoomChats -> chat.getRoomList(apiQ.contentID, apiQ.page, apiQ.pageSize)
            ApiType.SendChat -> chat.post(apiQ.contentID, "", apiQ.requestData as? String)
            ApiType.DeleteChat, ApiType.DeleteAllChat -> chat.delete(apiQ.contentID)
            ApiType.GetChatRooms -> chat.getRoom(apiQ.page, apiQ.pageSize)
            ApiType.DeleteChatRoom -> chat.deleteRoom(apiQ.contentID)
            ApiType.ReadChatRoom -> chat.putRoom(apiQ.contentID)
            ApiType.GetRecommandationFriends -> recommendation.getFriends()
        }
    }

    private fun getUpdateUserProfile(userId:String?, model:ModifyUserProfileData?) = runBlocking {
        var image: MultipartBody.Part? = null
        model?.image?.let {
            val file = it.toFile(context, "profileImage.jpg")
            val imgBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            image = MultipartBody.Part.createFormData("contents", file.name, imgBody)
        }
        val name: RequestBody? = getRequestBody(model?.nickName)
        val birthdate: RequestBody? = getRequestBody(model?.birth?.toDateFormatter()?.substring(0, 19))
        val sex: RequestBody? = getRequestBody(model?.gender?.apiDataKey)
        val introduce: RequestBody? = getRequestBody(model?.introduction)
        user.put(userId ?: "", name, birthdate, sex, introduce, image)
    }

    private fun getUpdatePetProfile(petId:String, img: Bitmap?) = runBlocking {
        var image: MultipartBody.Part? = null
        img?.let {
            val file = it.toFile(context)
            val imgBody: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            image = MultipartBody.Part.createFormData("contents", "profileImage.jpg" , imgBody)
        }
        pet.put(petId, contents = image)
    }

    private fun getUpdateRepresentative(petId:String) = runBlocking {
        val isRepresentative: RequestBody? = getRequestBody("true")
        pet.put(petId, isRepresentative = isRepresentative)
    }
    private fun getUpdatePetProfile(petId:String, profile: ModifyPetProfileData?) = runBlocking {
        val name: RequestBody? = getRequestBody(profile?.name)
        val breed: RequestBody? = getRequestBody(profile?.breed)
        val birthdate: RequestBody? = getRequestBody(profile?.birth?.toDateFormatter()?.substring(0, 19))
        val sex: RequestBody? = getRequestBody(profile?.gender?.apiDataKey)
        val regNumber: RequestBody? = getRequestBody(profile?.microchip)
        val animalId: RequestBody? = getRequestBody(profile?.animalId)
        val introduce: RequestBody? = getRequestBody(profile?.introduction)
        val weight: RequestBody? = getRequestBody(profile?.weight?.toString())
        val size: RequestBody? = getRequestBody(profile?.size?.toString())
        val isNeutralized: RequestBody? = getRequestBody(profile?.isNeutralized?.toString())
        val isRepresentative: RequestBody? = getRequestBody(profile?.isRepresentative?.toString())
        val tagBreed: RequestBody? = getRequestBody(profile?.breed)
        val tagPersonality: RequestBody? = getRequestBody(profile?.hashStatus)
        val tagStatus: RequestBody? = getRequestBody(profile?.immunStatus)
        pet.put(petId, name, breed, birthdate, sex, regNumber, animalId, introduce, weight, size,
            isNeutralized, isRepresentative,
            tagBreed, tagStatus, tagPersonality)
    }

    private fun getRegistPetProfile(userId:String?, profile: ModifyPetProfileData?) = runBlocking {
        val name: RequestBody? = getRequestBody(profile?.name)
        val breed: RequestBody? = getRequestBody(profile?.breed)
        val birthdate: RequestBody? = getRequestBody(profile?.birth?.toDateFormatter())
        val sex: RequestBody? = getRequestBody(profile?.gender?.apiDataKey)
        val regNumber: RequestBody? = getRequestBody(profile?.microchip)
        val animalId: RequestBody? = getRequestBody(profile?.animalId)
        val level: RequestBody? = getRequestBody("1")
        val isNeutralized: RequestBody? = getRequestBody(profile?.isNeutralized?.toString())
        val isRepresentative: RequestBody? = getRequestBody(profile?.isRepresentative?.toString())
        val tagBreed: RequestBody? = getRequestBody(profile?.breed)
        val tagPersonality: RequestBody? = getRequestBody(profile?.hashStatus)
        val tagStatus: RequestBody? = getRequestBody(profile?.immunStatus)

        var image: MultipartBody.Part? = null
        profile?.image?.let {
            val file = it.toFile(context)
            val imgBody: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            image = MultipartBody.Part.createFormData("contents", "profileImage.jpg" , imgBody)
        }
        pet.post(userId,name, breed, birthdate, sex, regNumber, animalId, isNeutralized, isRepresentative,  level,
            tagBreed, tagStatus, tagPersonality, image)
    }

    private fun getRegistAlbumPicture(ownerId:String?, userId:String?, albumData: AlbumData?) = runBlocking {
        val owner: RequestBody? = getRequestBody(ownerId)
        val user: RequestBody? = getRequestBody(userId)
        val type: RequestBody? = getRequestBody(albumData?.type?.getApiCode)
        val isExpose: RequestBody? = getRequestBody(albumData?.isExpose.toString())
        val referenceId: RequestBody? = getRequestBody(albumData?.referenceId)
        var image: MultipartBody.Part? = null
        var thumbImage:Bitmap? = albumData?.thumb
        albumData?.image?.let {
            val images = create(it)
            val file = images.first.toFile(context)
            val imgBody: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            image = MultipartBody.Part.createFormData("contents", "albumImage.jpg" , imgBody)
            if (albumData.thumb == null) thumbImage = images.second
        }
        var thumb: MultipartBody.Part? = null
        thumbImage?.let {
            val file = it.toFile(context)
            val imgBody: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            thumb = MultipartBody.Part.createFormData("smallContents", "thumbAlbumImage.jpg" , imgBody)
        }
        album.post(owner, type, user, isExpose, referenceId, thumb, image)
    }



    private fun getUpdateLikeAlbumPicture(id:String, isLike:Boolean? = null) = runBlocking {
        val param = HashMap<String, Any>()
        param["id"] = id.toIntOrNull() ?: 0
        param["isChecked"] = isLike ?: true
        val params = java.util.HashMap<String, Any>()
        params["items"] = arrayOf(param)
        album.putThumbsup(params)
    }
    private fun getUpdateExposeAlbumPicture(id:String, isExpose:Boolean? = null) = runBlocking {
        val param = HashMap<String, Any>()
        param["id"] = id.toIntOrNull() ?: 0
        param["isExpose"] = isExpose ?: true
        val params = java.util.HashMap<String, Any>()
        params["items"] = arrayOf(param)
        album.put(params)
    }

    private fun getReport(userId:String, type:ReportType, postId:String? = null) = runBlocking {
        val params = HashMap<String, String>()
        params["reportType"] = type.apiCoreKey
        postId?.let { params["postId"] = it }
        params["refUserId"] = userId
        misc.report(params)
    }

    private fun getVisionCheck(data:Bitmap?) = runBlocking {
        var image: MultipartBody.Part? = null
        data?.let {resource->
            val file = resource.toFile(context)
            val imgBody: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            image = MultipartBody.Part.createFormData("contents", "visionImage.jpg" , imgBody)
        }
        vision.post(image)
    }

    private fun getUpdateWalk(id:String, data:WalkadditionalData?) = runBlocking {
        val lat: RequestBody? = getRequestBody(data?.loc?.latitude.toString())
        val lng: RequestBody? = getRequestBody(data?.loc?.longitude.toString())
        val status: RequestBody? = getRequestBody(data?.status?.name)
        val duration: RequestBody? = getRequestBody(data?.walkTime?.toInt()?.toString())
        val distance: RequestBody? = getRequestBody(data?.walkDistance?.toInt()?.toString())
        var image: MultipartBody.Part? = null
        var thumb: MultipartBody.Part? = null
        data?.img?.let {img->
            val images = create(img)
            val file = images.first.toFile(context)
            val imgBody: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            image = MultipartBody.Part.createFormData("contents", "albumImage.jpg" , imgBody)
            val secondFile = images.second.toFile(context)
            val secondImgBody: RequestBody = secondFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            thumb = MultipartBody.Part.createFormData("smallContents", "thumbAlbumImage.jpg" , secondImgBody)
        }
        walk.put(id, lat, lng, status, duration, distance, thumb, image)
    }
    private  fun getRequestBody(value:String?):RequestBody?{
        value ?: return null
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }
    suspend fun create(img:Bitmap):Pair<Bitmap, Bitmap>{
        return withContext(Dispatchers.IO) {
            val hei = DimenApp.originImageSize * img.height / img.width
            val crop = Size(img.width, img.height)
                .getCropRatioSize(
                    Size(
                        DimenApp.thumbImageSize,
                        DimenApp.thumbImageSize
                    )
                )
            val originImg = img.size(DimenApp.originImageSize, hei)
            val thumbImg = img.centerCrop(crop)
            return@withContext Pair(originImg, thumbImg)
        }
    }
}