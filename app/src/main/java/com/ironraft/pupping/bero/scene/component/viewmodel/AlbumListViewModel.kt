package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.scene.component.item.AlbumListItemData
import com.ironraft.pupping.bero.scene.component.list.AlbumListType
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.api.rest.AlbumData
import com.ironraft.pupping.bero.store.api.rest.PictureData
import com.lib.page.ListViewModel
import java.util.*

open class AlbumListViewModel(val repo: PageRepository)
    :ListViewModel<AlbumListItemData,List<PictureData>>() {

    var currentId:String = ""
    var currentType:AlbumCategory = AlbumCategory.User
    var limitedSize:Int? = null
    fun initSetup(owner: LifecycleOwner, pageSize:Int, limitedSize:Int? = null): AlbumListViewModel {
        this.pageSize = pageSize
        this.limitedSize = limitedSize
        setDefaultLifecycleOwner(owner)
        return this
    }

    fun lazySetup(id: String? = null, type: AlbumCategory? = null): AlbumListViewModel {
        id?.let { currentId = it }
        type?.let { currentType = it }
        return this
    }

    override fun onReset(){
    }

    override fun onLoad(page: Int) {
        val query = HashMap<String,String>()
        query[ApiField.pictureType] = currentType.getApiCode
        val q = ApiQ(tag,
            ApiType.GetAlbumPictures,
            contentID = currentId,
            page = currentPage,
            pageSize = pageSize,
            query = query,
            requestData = currentType)
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<AlbumListItemData>?, addDatas: List<PictureData>?): List<AlbumListItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<AlbumListItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            AlbumListItemData().setData(d,  idx = start + idx)
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

    fun deleteAll():Boolean{
        listDatas.value?.let { datas->
            val selects = datas.filter { it.isDelete.value == true }
            if(selects.isEmpty()) return false
            val del = selects.fold("") { prev, cur ->
                if(prev.isEmpty()) cur.pictureId.toString()
                else prev + "," + cur.pictureId.toString()
            }
            val query = HashMap<String,String>()
            query[ApiField.pictureIds] = del
            val q = ApiQ(tag,
                ApiType.DeleteAlbumPictures,
                contentID = currentId,
                page = currentPage,
                pageSize = pageSize,
                query = query,
                requestData = currentType)
            repo.dataProvider.requestData(q)
            return true
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            if(res.contentID != currentId) return@observe
            (res.requestData as? AlbumCategory)?.let { cate->
                if(cate != currentType) return@observe
            }

            when ( res.type ){
                ApiType.GetAlbumPictures -> {
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<PictureData> ?: listOf())
                }
                ApiType.RegistAlbumPicture , ApiType.DeleteAlbumPictures ->{
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
                ApiType.GetAlbumPictures -> {
                    isBusy = false
                }
                ApiType.RegistAlbumPicture ->{}
                else ->{}
            }
        }
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }

}