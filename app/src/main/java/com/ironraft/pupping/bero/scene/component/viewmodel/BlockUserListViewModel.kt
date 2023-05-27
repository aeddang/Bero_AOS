package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.scene.component.item.BlockUserItemData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.UserData
import com.lib.page.ListViewModel
import java.util.*

open class BlockUserListViewModel(val repo: PageRepository)
    :ListViewModel<BlockUserItemData,List<UserData>>() {
    val currentId:String = repo.dataProvider.user.userId ?: ""


    fun initSetup(owner: LifecycleOwner, pageSize:Int): BlockUserListViewModel {
        this.pageSize = pageSize
        setDefaultLifecycleOwner(owner)
        return this
    }


    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetBlockUsers,
            contentID = currentId,
            page = currentPage,
            pageSize = pageSize)
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<BlockUserItemData>?, addDatas: List<UserData>?): List<BlockUserItemData> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<BlockUserItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            BlockUserItemData().setData(d,  idx = start + idx)
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
                ApiType.GetBlockUsers -> {
                    if(res.contentID != currentId) return@observe
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<UserData> ?: listOf())
                }
                ApiType.RequestBlock -> {
                    reset()
                    load()
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            if(err.contentID != currentId) return@observe
            when ( err.type ){
                ApiType.GetBlockUsers -> {
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