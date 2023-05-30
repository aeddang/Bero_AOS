package com.ironraft.pupping.bero.store.api.rest

import com.google.gson.annotations.SerializedName
import com.ironraft.pupping.bero.store.api.Api
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiResponse
import com.ironraft.pupping.bero.store.api.ApiValue
import retrofit2.http.*


interface ChatApi {
    @GET(Api.Chat.chat)
    suspend fun get(
        @Query(ApiField.otherUser) otherUser: String?,
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<ChatData>?

    @GET(Api.Chat.chatRoomList)
    suspend fun getRoomList(
        @Path(Api.CONTENT_ID) contentID: String,
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<ChatsData>?

    @POST(Api.Chat.chatSend)
    suspend fun post(
        @Query(ApiField.receiver) receiver: String?,
        @Query(ApiField.title) title: String?,
        @Query(ApiField.contents) contents: String?
    ): ApiResponse<ChatsData>?

    @DELETE(Api.Chat.chat)
    suspend fun delete(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<Any>?

    @GET(Api.Chat.chatRooms)
    suspend fun getRoom(
        @Query(ApiField.page) page: Int? = 0,
        @Query(ApiField.size) size: Int? = ApiValue.PAGE_SIZE
    ): ApiResponse<ChatRoomData>?

    @PUT(Api.Chat.chatRoomRead)
    suspend fun putRoom(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<Any>?

    @DELETE(Api.Chat.chatRoom)
    suspend fun deleteRoom(
        @Path(Api.CONTENT_ID) contentID: String
    ): ApiResponse<Any>?

}

data class ChatRoomData(
    @SerializedName("chatRoomId") var chatRoomId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("unreadCnt") var unreadCnt:Int? = null,
    @SerializedName("sender") var sender: String? = null,
    @SerializedName("receiver") var receiver: UserData? = null,
    @SerializedName("receiverPet") var receiverPet: PetData? = null
)

data class ChatsData(
    @SerializedName("receiveUser") var receiveUser: UserData? = null,
    @SerializedName("receivePet") var receivePet: PetData? = null,
    @SerializedName("chats") var chats:List<ChatData>? = null
)

data class ChatData(
    @SerializedName("chatId") var chatId: Int? = null,
    @SerializedName("chatRoomId") var chatRoomId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("contents") var contents: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("receiver") var receiver: String? = null,
    @SerializedName("isRead") var isRead: Boolean? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null
)


