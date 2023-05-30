package com.ironraft.pupping.bero.scene.page.chat.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.page.chat.component.ChatItemData
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.ChatData
import com.ironraft.pupping.bero.store.api.rest.ChatsData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.page.AppObserver
import com.lib.page.ListViewModel
import com.lib.util.toFormatString
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


data class ChatListDataSet(
    var index:Int = -1,
    var date:LocalDate? = null,
    var originDate:LocalDate?= null,
    var isMe:Boolean = false,
    var datas:ArrayList<ChatItemData> = arrayListOf()
)

open class ChatRoomViewModel(val repo: PageRepository)
    :ListViewModel<ChatItemData,List<ChatData>>() {
    var currentRoomId:String = ""
    var currentUserId:String = ""
    var me = repo.dataProvider.user.userId ?: ""
    val chats:MutableLiveData<List<ChatListDataSet>> = MutableLiveData(listOf())
    val user:MutableLiveData<User?> = MutableLiveData(null)
    val pet:MutableLiveData<PetProfile?> = MutableLiveData(null)

    fun initSetup(owner: LifecycleOwner, pageSize:Int): ChatRoomViewModel {
        this.pageSize = pageSize
        setDefaultLifecycleOwner(owner)
        return this
    }
    fun resetLoad(){
        currentPage = 0
        reset()
        load()
    }

    override fun onReset() {
        chats.value = listOf()
    }
    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetRoomChats,
            contentID = currentRoomId,
            page = page,
            pageSize = pageSize
        )
        repo.dataProvider.requestData(q)
    }
    override fun onLoaded(prevDatas: List<ChatItemData>?, addDatas: List<ChatData>?): List<ChatItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<ChatItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            ChatItemData().setData(d, me = me, idx = start + idx)
        }
        isLoadCompleted = datas.count() < pageSize
        isEmpty.value = added.isEmpty()
        return added
    }

    override fun onLoadedEnd(added: List<ChatItemData>?) {
        val currentAdded = added ?: return
        val chats:ArrayList<ChatListDataSet> = arrayListOf()
        this.chats.value?.let { chats.addAll(it) }

        var index = chats.count()
        val addedChat:ArrayList<ChatListDataSet> = arrayListOf()
        var isFirst:Boolean = chats.isEmpty()
        var currentDataSet = chats.firstOrNull() ?: ChatListDataSet()
        currentAdded.forEach{ add ->
            val ymd = currentDataSet.originDate?.toFormatString("yyyyMMdd")
            val chatYmd = add.date?.toFormatString("yyyyMMdd")
            val isMe = currentDataSet.isMe
            if (isFirst) {
                isFirst = false
                currentDataSet.originDate = add.date
                currentDataSet.index = 0
                index += 1
                currentDataSet.isMe = add.isMe
                currentDataSet.datas.add(add)
                addedChat.add(currentDataSet)

            } else if (add.isMe != isMe || ymd != chatYmd){
                val prev = currentDataSet
                currentDataSet = ChatListDataSet()
                currentDataSet.index = index
                index += 1
                currentDataSet.originDate = add.date
                if (ymd != chatYmd) {
                    prev.date = prev.originDate
                }
                currentDataSet.isMe = add.isMe
                currentDataSet.datas.add(add)
                addedChat.add(currentDataSet)
            } else {
                currentDataSet.datas.add(add)
            }
        }
        chats.addAll (0, addedChat.reversed())
        this.chats.value = chats
        /*
        self.infinityScrollModel.onComplete(itemCount: added.count)
        if self.infinityScrollModel.page == 1 , let last = self.chats.last {
            self.infinityScrollModel.uiEvent = .scrollTo(last.hashId, .center)
        }*/
    }

    fun insertChat(data:ChatData){
        val add = ChatItemData().setData(data, me = me, idx = 0)
        val chats:ArrayList<ChatListDataSet> = arrayListOf()
        this.chats.value?.let { chats.addAll(it) }

        var isFirst:Boolean = chats.isEmpty()
        var currentDataSet = chats.lastOrNull() ?: ChatListDataSet()
        val ymd = currentDataSet.originDate?.toFormatString("yyyyMMdd")
        val chatYmd = add.date?.toFormatString("yyyyMMdd")
        val isMe = currentDataSet.isMe
        if (isFirst) {
            isFirst = false
            currentDataSet.originDate = add.date
            currentDataSet.index = 0
            currentDataSet.isMe = add.isMe
            currentDataSet.datas.add(add)
            chats.add(currentDataSet)

        } else if (add.isMe != isMe || ymd != chatYmd){
            val prev = currentDataSet
            currentDataSet.index = 1
            currentDataSet = ChatListDataSet()
            currentDataSet.index = 0
            currentDataSet.originDate = add.date
            if (ymd != chatYmd) {
                prev.date = prev.originDate
            }
            currentDataSet.isMe = add.isMe
            currentDataSet.datas.add(add)
            chats.add(currentDataSet)
        } else {
            currentDataSet.datas.add(0,add)
            chats.removeLast()
            chats.add(currentDataSet)
        }
        this.chats.value = chats

        //self.infinityScrollModel.uiEvent = .scrollTo(currentDataSet.hashId)

    }

    fun delete(chatId:Int){
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_chatDeleteConfirm),
            text = repo.pagePresenter.activity.getString(R.string.alert_chatDeleteConfirmText),
            isNegative = true,
            buttons = arrayListOf(
                repo.pagePresenter.activity.getString(R.string.cancel),
                repo.pagePresenter.activity.getString(R.string.button_delete)
            )
        ){
            if(it != 1) return@ActivitSheetEvent
            val q = ApiQ(tag,
                ApiType.DeleteChat,
                contentID = chatId.toString()
            )
            repo.dataProvider.requestData(q)
        }
    }
    fun read(){
        val q = ApiQ(tag,
            ApiType.ReadChatRoom,
            contentID = currentRoomId,
            isOptional = true
        )
        repo.dataProvider.requestData(q)
    }
    fun exit(){
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_chatRoomDeleteConfirm),
            text = repo.pagePresenter.activity.getString(R.string.alert_chatRoomDeleteConfirmText),
            isNegative = false,
            buttons = arrayListOf(
                repo.pagePresenter.activity.getString(R.string.cancel),
                repo.pagePresenter.activity.getString(R.string.button_delete)
            )
        ){
            if(it != 1) return@ActivitSheetEvent
            val q = ApiQ(tag,
                ApiType.GetChatRooms,
                contentID = currentRoomId
            )
            repo.dataProvider.requestData(q)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            when ( res.type ){
                ApiType.GetRoomChats -> {
                    if(res.page == 0) { reset() }
                    val data = res.data as? ChatsData
                    data?.receiveUser?.let { userData ->
                        user.value = User().setData(userData)
                    }
                    data?.receivePet?.let { petData ->
                        pet.value = PetProfile().init(petData)
                    }
                    loaded(data?.chats ?: listOf())
                }
                ApiType.SendChat ->{
                    if(currentUserId != res.contentID) return@observe
                    val data = res.data as? ChatData ?: ChatData(
                        contents = res.requestData as? String,
                        createdAt = LocalDate.now().toFormatString(),
                        receiver = currentUserId
                    )
                    insertChat(data)
                }
                ApiType.DeleteChat ->{
                    val chatId = res.contentID
                    listDatas.value?.let { lists ->
                        lists.find { list-> list.chatId.toString() == chatId }?.let {chat->
                            chat.isDelete.value = true
                        }
                    }
                }
                ApiType.DeleteChatRoom, ApiType.RequestBlock ->
                    repo.pagePresenter.goBack()
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            when ( err.type ){
                ApiType.GetRoomChats -> {
                    isBusy = false
                }
                else ->{}
            }
        }
        AppObserver.pageApns.observe(owner){ apns ->
            apns?.let { pageApns ->
                val pageId = pageApns.page.pageID
                when(pageId){
                    PageID.Chat.value -> {
                        resetLoad()
                        read()
                    }
                    else ->{}
                }
            }
        }
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }

}