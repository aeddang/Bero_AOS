package com.ironraft.pupping.bero.scene.component.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.lib.page.ComponentViewModel
import java.util.*

open class FriendFunctionViewModel(val repo: PageRepository, id:String, initStatus:FriendStatus? = null) :ComponentViewModel() {

    var currentId:String = id
    var currentStatus = MutableLiveData<FriendStatus>(initStatus ?: FriendStatus.Norelation)
    fun initSetup(owner: LifecycleOwner): FriendFunctionViewModel {
        setDefaultLifecycleOwner(owner)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            if(res.contentID != currentId) return@observe
            when ( res.type ){
                ApiType.RequestFriend -> {
                    currentStatus.value = FriendStatus.RequestFriend
                }
                ApiType.AcceptFriend -> {
                    currentStatus.value = FriendStatus.Friend
                }
                ApiType.RejectFriend, ApiType.DeleteFriend -> {
                    currentStatus.value = FriendStatus.Norelation
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