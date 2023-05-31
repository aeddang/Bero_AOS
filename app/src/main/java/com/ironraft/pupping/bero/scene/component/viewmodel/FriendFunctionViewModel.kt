package com.ironraft.pupping.bero.scene.component.viewmodel

import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.SceneEvent
import com.ironraft.pupping.bero.SceneEventType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.lib.page.ComponentViewModel
import com.lib.util.replace
import com.lib.util.showCustomToast
import java.util.*

open class FriendFunctionViewModel(val repo: PageRepository, id:String = "", initStatus:FriendStatus? = null) :ComponentViewModel() {

    var currentId:String = id
    var currentStatus = MutableLiveData<FriendStatus>(initStatus ?: FriendStatus.Norelation)
    var isMe:Boolean = repo.dataProvider.user.isSameUser(id); private set
    fun initSetup(owner: LifecycleOwner): FriendFunctionViewModel {
        setDefaultLifecycleOwner(owner)
        return this
    }
    fun lazySetup(id: String? = null, status: FriendStatus? = null): FriendFunctionViewModel {
        id?.let { currentId = it }
        status?.let { currentStatus.value = it }
        isMe = repo.dataProvider.user.isSameUser(id)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            if(res.contentID != currentId) return@observe
            val activity = repo.pagePresenter.activity
            when ( res.type ){
                ApiType.RequestFriend -> {
                    currentStatus.value = FriendStatus.RequestFriend
                    Toast(activity).showCustomToast(
                        activity.getString(R.string.alert_friendRequest),
                        activity
                    )
                }
                ApiType.AcceptFriend -> {
                    currentStatus.value = FriendStatus.Friend
                    Toast(activity).showCustomToast(
                        activity.getString(R.string.alert_friendAccept),
                        activity
                    )
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
    fun requestFriend(){
        val q = ApiQ(tag, ApiType.RequestFriend,  contentID = currentId)
        repo.dataProvider.requestData(q)
    }
    fun acceptFriend(){
        val q = ApiQ(tag, ApiType.AcceptFriend,  contentID = currentId)
        repo.dataProvider.requestData(q)
    }

    fun rejectFriend(){
        val q = ApiQ(tag, ApiType.RejectFriend,  contentID = currentId)
        repo.dataProvider.requestData(q)
    }
    fun sendChat(){
        repo.appSceneObserver.event.value = SceneEvent(SceneEventType.SendChat, value = currentId)
    }
    fun removeFriend(userName:String? = null, friendId:String? = null){
        val ac = repo.pagePresenter.activity
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title =  ac.getString(R.string.alert_friendDeleteConfirm).replace(userName ?: ""),
            text = ac.getString(R.string.alert_friendDeleteConfirmText),
            buttons = arrayListOf(ac.getString(R.string.cancel), ac.getString(R.string.button_removeFriend)),
            isNegative = true
        ){
            if (it == 1) {
                val q = ApiQ(tag, ApiType.DeleteFriend,  contentID = friendId ?: currentId)
                repo.dataProvider.requestData(q)
            }
        }
    }

}