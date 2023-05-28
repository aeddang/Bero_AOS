package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.scene.component.item.FriendListItemData
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.api.rest.ExplorerSearchType
import com.ironraft.pupping.bero.store.api.rest.FriendData
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.lib.page.ListViewModel
import java.util.*
import kotlin.math.round

open class FriendListViewModel(val repo: PageRepository, id:String = "", initType: FriendListType = FriendListType.Friend)
    :ListViewModel<FriendListItemData,List<FriendData>>() {

    var currentId:String = id
    var currentType:FriendListType = initType
    var limitedSize:Int? = null
    var isMe:Boolean = repo.dataProvider.user.isSameUser(id); private set
    val hasRequested = MutableLiveData<Boolean>(false)
    fun initSetup(owner: LifecycleOwner, pageSize:Int, limitedSize:Int? = null): FriendListViewModel {
        this.pageSize = pageSize
        this.limitedSize = limitedSize
        setDefaultLifecycleOwner(owner)
        return this
    }

    fun lazySetup(id: String? = null, type: FriendListType? = null): FriendListViewModel {
        id?.let { currentId = it }
        type?.let { currentType = it }
        isMe = repo.dataProvider.user.isSameUser(currentId);
        return this
    }

    fun resetLoad(type: FriendListType){
        currentType = type
        currentPage = 0
        reset()
        load()
    }

    fun checkRequeste(){
        val q = ApiQ(tag,
            ApiType.CheckRequestFriends,
            contentID = currentId,
            page = 0,
            pageSize = 1,
            requestData = currentType)
        repo.dataProvider.requestData(q)
    }

    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            currentType.apiType,
            contentID = currentId,
            page = page,
            pageSize = pageSize,
            requestData = currentType)
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<FriendListItemData>?, addDatas: List<FriendData>?): List<FriendListItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<FriendListItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            FriendListItemData().setData(d,  idx = start + idx, type = currentType.status)
        }
        limitedSize?.let { max->
            if(added.count() > max){
                added = added.slice(0 until max)
            }
        }
        isLoadCompleted = datas.count() < pageSize
        isEmpty.value = added.isEmpty()
        return added
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            if(res.contentID != currentId) return@observe
            if(res.requestData != currentType) return@observe
            when ( res.type ){
                ApiType.GetFriends -> {
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<FriendData> ?: listOf())
                }
                ApiType.GetRequestFriends -> {
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<FriendData> ?: listOf())
                }
                ApiType.GetRequestedFriends -> {
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<FriendData> ?: listOf())
                }
                ApiType.CheckRequestFriends -> {
                    val datas = res.data as? List<FriendData> ?: listOf()
                    hasRequested.value = datas.isNotEmpty()
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            if(err.contentID != currentId) return@observe
            if(err.requestData != currentType) return@observe
            when ( err.type ){
                ApiType.GetFriends, ApiType.GetRequestFriends, ApiType.GetRequestedFriends -> {
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