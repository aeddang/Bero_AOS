package com.ironraft.pupping.bero.scene.component.viewmodel
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.ReportType
import com.lib.page.ComponentViewModel
import com.lib.util.replace
import com.lib.util.showCustomToast
import com.skeleton.component.dialog.RadioBtnData
import java.util.*

open class ReportFunctionViewModel(val repo: PageRepository, id:String = "", name:String? = null, initIsBlock:Boolean = false) :ComponentViewModel() {

    var currentUserId:String = id
    var currentName:String? = name
    var isMe:Boolean = repo.dataProvider.user.isSameUser(id)
    var isBlock = MutableLiveData<Boolean>(initIsBlock)
    fun initSetup(owner: LifecycleOwner): ReportFunctionViewModel {
        setDefaultLifecycleOwner(owner)
        return this
    }

    fun lazySetup(id: String? = null, name:String? = null): ReportFunctionViewModel {
        id?.let { currentUserId = it }
        name?.let { currentName = it }
        isMe = repo.dataProvider.user.isSameUser(id)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            if(res.contentID != currentUserId) return@observe
            val activity = repo.pagePresenter.activity
            when ( res.type ){
                ApiType.RequestBlock -> {
                    val block = res.requestData as? Boolean ?: false
                    isBlock.value = block
                    Toast(activity).showCustomToast(
                        activity.getString(if (block) R.string.alert_blockUserCompleted else  R.string.alert_unblockUserCompleted),
                        activity
                    )
                }
                ApiType.PostReport -> {
                    Toast(activity).showCustomToast(
                        ReportType.Post.completeMessage,
                        activity
                    )
                }
                ApiType.Report -> {
                    val reportYype = res.requestData as? ReportType ?: ReportType.User
                    Toast(activity).showCustomToast(
                        reportYype.completeMessage,
                        activity
                    )
                }
                else ->{}
            }
        }
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }

    fun more(type:ReportType, postId:String? = null){
        repo.appSceneObserver.radio.value = ActivitRadioEvent(
            type = ActivitRadioType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_supportAction),
            radioButtons = arrayListOf(
                RadioBtnData(
                    icon = R.drawable.block,
                    title = repo.pagePresenter.activity.getString(R.string.button_block),
                    index = 0
                ),
                RadioBtnData(
                    icon = R.drawable.warning,
                    title = repo.pagePresenter.activity.getString(R.string.button_accuse),
                    index = 1
                )
            )
        ){ select ->
            when(select){
                0 -> block()
                1 -> {
                    when(type){
                        ReportType.Post -> accusePost(postId)
                        else -> accuseUser(type)
                    }
                }
                else -> {}
            }
        }
    }

    fun block(){
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_blockUserConfirm).replace(currentName ?: ""),
            text = repo.pagePresenter.activity.getString(R.string.alert_blockUserConfirmText),
            isNegative = true,
            buttons = arrayListOf(
                repo.pagePresenter.activity.getString(R.string.cancel),
                repo.pagePresenter.activity.getString(R.string.button_block)
            )
        ){
            if(it == 1){
                val q = ApiQ(tag, ApiType.RequestBlock,  contentID = currentUserId, requestData = true)
                repo.dataProvider.requestData(q)
            }
        }
    }
    fun unblock(){
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_unblockUserConfirm).replace(currentName ?: ""),
            isNegative = false,
            buttons = arrayListOf(
                repo.pagePresenter.activity.getString(R.string.cancel),
                repo.pagePresenter.activity.getString(R.string.button_unblock)
            )
        ){
            if(it == 1){
                val q = ApiQ(tag, ApiType.RequestBlock,  contentID = currentUserId, requestData = false)
                repo.dataProvider.requestData(q)
            }
        }
    }

    fun accuseUser(type:ReportType = ReportType.User){
        val name = currentName ?: ""
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_accuseUserConfirm).replace(name),
            text = repo.pagePresenter.activity.getString(R.string.alert_accuseUserConfirmText),
            isNegative = true,
            buttons = arrayListOf(
                repo.pagePresenter.activity.getString(R.string.cancel),
                repo.pagePresenter.activity.getString(R.string.button_accuseUser)
            )
        ){
            if(it == 1){
                val q = ApiQ(tag, ApiType.Report,  contentID = currentUserId, requestData = type)
                repo.dataProvider.requestData(q)
            }
        }
    }

    fun accusePost(postId:String?= null){
        repo.appSceneObserver.sheet.value = ActivitSheetEvent(
            type = ActivitSheetType.Select,
            title = repo.pagePresenter.activity.getString(R.string.alert_accuseAlbumConfirm),
            text = repo.pagePresenter.activity.getString(R.string.alert_accuseAlbumConfirmText),
            isNegative = true,
            buttons = arrayListOf(
                repo.pagePresenter.activity.getString(R.string.cancel),
                repo.pagePresenter.activity.getString(R.string.button_accuse)
            )
        ){
            if(it == 1){
                val q = ApiQ(tag, ApiType.PostReport,  contentID = currentUserId, requestData = postId)
                repo.dataProvider.requestData(q)
            }
        }
    }
}