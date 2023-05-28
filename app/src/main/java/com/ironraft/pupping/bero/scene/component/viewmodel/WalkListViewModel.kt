package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.scene.component.item.WalkListItemData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.lib.page.ListViewModel
import java.util.*

open class WalkListViewModel(val repo: PageRepository, id:String = "")
    :ListViewModel<WalkListItemData,List<WalkData>>() {
    var currentId:String = id

    fun initSetup(owner: LifecycleOwner, pageSize:Int): WalkListViewModel {
        this.pageSize = pageSize
        setDefaultLifecycleOwner(owner)
        return this
    }
    fun lazySetup(id: String? = null): WalkListViewModel {
        id?.let { currentId = it }
        return this
    }

    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetUserWalks,
            contentID = currentId,
            page = page,
            pageSize = pageSize)
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<WalkListItemData>?, addDatas: List<WalkData>?): List<WalkListItemData> {
        val activity = repo.pagePresenter.activity
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<WalkListItemData> = listOf()
        val start = prev.count()
        added = datas.mapIndexed { idx, d  ->
            WalkListItemData().setData(d,  idx = start + idx, ctx = activity )
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
                ApiType.GetUserWalks -> {
                    if(res.contentID != currentId) return@observe
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<WalkData> ?: listOf())
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            if(err.contentID != currentId) return@observe
            when ( err.type ){
                ApiType.GetUserWalks -> {
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