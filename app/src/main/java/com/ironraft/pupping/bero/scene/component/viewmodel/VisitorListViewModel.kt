package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.api.rest.UserAndPet
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.ListViewModel
import java.util.*

open class VisitorListViewModel(val repo: PageRepository, id:String = "")
    :ListViewModel<PetProfile,List<UserAndPet>>() {
    var currentId:String = id

    fun initSetup(owner: LifecycleOwner, pageSize:Int): VisitorListViewModel {
        this.pageSize = pageSize
        setDefaultLifecycleOwner(owner)
        return this
    }
    fun lazySetup(id: String? = null): VisitorListViewModel {
        id?.let { currentId = it }
        return this
    }

    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetPlaceVisitors,
            contentID = currentId,
            page = page,
            pageSize = pageSize)
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<PetProfile>?, addDatas: List<UserAndPet>?): List<PetProfile> {
        val prev = prevDatas ?: listOf()
        val datas = addDatas ?: listOf()
        var added:List<PetProfile> = listOf()
        val start = prev.count()
        val me = repo.dataProvider.user.userId
        added = datas.mapIndexed { idx, d  ->
            PetProfile().init(
                d.pet ?: PetData(),
                userId = d.user?.userId,
                isMyPet = me == d.user?.userId,
                isFriend = d.user?.isFriend ?: false,
                lv = d.user?.level,
                index = start + idx
            )
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
                ApiType.GetPlaceVisitors -> {
                    if(res.contentID != currentId) return@observe
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<UserAndPet> ?: listOf())
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            if(err.contentID != currentId) return@observe
            when ( err.type ){
                ApiType.GetPlaceVisitors -> {
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