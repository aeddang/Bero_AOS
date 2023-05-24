package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.scene.component.item.UserAlbumListItemData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.ExplorerSearchType
import com.ironraft.pupping.bero.store.api.rest.PictureData
import com.lib.page.ListViewModel
import com.lib.util.AppUtil
import java.time.LocalDateTime
import java.util.*
import kotlin.math.round

open class UserAlbumListViewModel(val repo: PageRepository)
    :ListViewModel<List<UserAlbumListItemData>,List<PictureData>>() {

    var currentId:String = round(System.currentTimeMillis()/1000.0).toInt().toString(); private set
    var currentType:ExplorerSearchType = ExplorerSearchType.All


    fun initSetup(owner: LifecycleOwner): UserAlbumListViewModel {
        this.pageSize = ApiValue.PAGE_SIZE
        setDefaultLifecycleOwner(owner)
        return this
    }

    fun resetLoad(type:ExplorerSearchType){
        currentId = round(System.currentTimeMillis()/1000.0).toInt().toString()
        currentType = type
        reset()
        load()
    }

    override fun onLoad(page: Int) {
        val query = HashMap<String,String>()
        query[ApiField.searchType] = currentType.getApiCode
        val q = ApiQ(tag,
            ApiType.GetExplorePictures,
            contentID = currentId,
            page = currentPage,
            pageSize = pageSize,
            query = query,
            requestData = currentType)
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<UserAlbumListItemData>?, addDatas: List<PictureData>?): List<UserAlbumListItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<UserAlbumListItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            UserAlbumListItemData().setData(d,  idx = start + idx)
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
            when ( res.type ){
                ApiType.GetExplorePictures -> {
                    if(res.contentID != currentId) return@observe
                    (res.requestData as? ExplorerSearchType)?.let { type->
                        if(type != currentType) return@observe
                    }
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<PictureData> ?: listOf())
                }
                ApiType.RegistAlbumPicture, ApiType.RequestBlock ->{ //ApiType.BlockUser
                    reset()
                    load()
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            if(err.contentID != currentId) return@observe
            if(err.requestData != currentType) return@observe
            when ( err.type ){
                ApiType.GetExplorePictures -> {
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