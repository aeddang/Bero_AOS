package com.ironraft.pupping.bero.scene.page.chat.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.scene.page.chat.component.ChatRoomListItemData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.ChatRoomData
import com.lib.page.ListViewModel
import java.util.*

open class ChatRoomListViewModel(val repo: PageRepository)
    :ListViewModel<ChatRoomListItemData,List<ChatRoomData>>() {

    fun initSetup(owner: LifecycleOwner, pageSize:Int): ChatRoomListViewModel {
        this.pageSize = pageSize
        setDefaultLifecycleOwner(owner)
        return this
    }
    fun resetLoad(){
        currentPage = 0
        reset()
        load()
    }
    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetChatRooms,
            page = page,
            pageSize = pageSize
        )
        repo.dataProvider.requestData(q)
    }
    override fun onLoaded(prevDatas: List<ChatRoomListItemData>?, addDatas: List<ChatRoomData>?): List<ChatRoomListItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<ChatRoomListItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            ChatRoomListItemData().setData(d,  idx = start + idx)
        }
        isLoadCompleted = datas.count() < pageSize
        return added
    }
    fun read(roomId:Int){
        val q = ApiQ(tag,
            ApiType.ReadChatRoom,
            contentID = roomId.toString(),
            isOptional = true
        )
        repo.dataProvider.requestData(q)
    }
    fun exit(roomId:Int){
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_chatRoomDeleteConfirm),
            text = repo.pagePresenter.activity.getString(R.string.alert_chatRoomDeleteConfirmText),
            isNegative = true,
            buttons = arrayListOf(
                repo.pagePresenter.activity.getString(R.string.cancel),
                repo.pagePresenter.activity.getString(R.string.button_delete)
            )
        ){
            if(it != 1) return@ActivitSheetEvent
            val q = ApiQ(tag,
                ApiType.DeleteChatRoom,
                contentID = roomId.toString()
            )
            repo.dataProvider.requestData(q)
        }
    }

    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            when ( res.type ){
                ApiType.GetChatRooms -> {
                    val lists = res.data as? List<*> ?: return@observe
                    val datas = lists.filterIsInstance<ChatRoomData>()
                    if(res.page == 0) { reset() }
                    loaded(datas)
                }
                ApiType.DeleteChatRoom ->{
                    reset()
                    load()
                }
                ApiType.ReadChatRoom ->{
                    val findId = res.contentID
                    val find = listDatas.value?.find { data -> data.roomId.toString() == findId }
                    find?.isRead?.value = true
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            when ( err.type ){
                ApiType.GetChatRooms -> {
                    isBusy = false
                }
                else ->{}
            }
        }
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }

}