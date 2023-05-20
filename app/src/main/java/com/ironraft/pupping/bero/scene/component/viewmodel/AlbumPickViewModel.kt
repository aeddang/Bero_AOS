package com.ironraft.pupping.bero.scene.component.viewmodel

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSelectEvent
import com.ironraft.pupping.bero.activityui.ActivitSelectType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlbumCategory
import com.ironraft.pupping.bero.store.api.rest.AlbumData
import com.lib.page.ComponentViewModel
import com.lib.page.PageEventType
import com.lib.page.PageRequestPermission
import com.lib.util.AppUtil
import com.lib.util.getBitmap
import java.util.*

open class AlbumPickViewModel(val repo: PageRepository):ComponentViewModel() {
    val requestId:Int = UUID.randomUUID().hashCode()
    var currentId:String = ""
    var currentType:AlbumCategory = AlbumCategory.User

    fun initSetup(owner: LifecycleOwner): AlbumPickViewModel {
        setDefaultLifecycleOwner(owner)
        return this
    }

    fun onPick(){
        repo.appSceneObserver.select.value = ActivitSelectEvent(
            type = ActivitSelectType.ImgPicker
        ){ select ->
            if (select == -1) return@ActivitSelectEvent
            when(select){
                0 ->
                    AppUtil.openIntentImagePick(repo.pagePresenter.activity, false, requestId)
                1 ->
                    repo.pagePresenter.requestPermission(
                        arrayOf(Manifest.permission.CAMERA),
                        requester = object : PageRequestPermission {
                            override fun onRequestPermissionResult(
                                resultAll: Boolean,
                                permissions: List<Boolean>?
                            ) {
                                if (!resultAll) return
                                AppUtil.openIntentImagePick(repo.pagePresenter.activity, true, requestId)
                            }
                        }
                    )
            }
        }
    }

    fun update(img: Bitmap, isExpose:Boolean){
        val album: AlbumData = AlbumData(type = currentType, image = img, isExpose=isExpose)
        val q = ApiQ(tag,
            ApiType.RegistAlbumPicture,
            contentID = currentId,
            requestData = album)
        repo.dataProvider.requestData(q)
    }

    fun updateConfirm(img: Bitmap){
        val isExpose = repo.storage.isExpose
        if (repo.storage.isExposeSetup) {
            update(img, isExpose)
        } else {
            repo.appSceneObserver.sheet.value = ActivitSheetEvent(
                type = ActivitSheetType.Select,
                text = repo.pagePresenter.activity.getString(R.string.alert_exposeConfirm),
                isNegative = false,
                buttons = arrayListOf(
                    repo.pagePresenter.activity.getString(R.string.alert_unExposed),
                    repo.pagePresenter.activity.getString(R.string.alert_exposed)
                )
            ){
                update(img, it == 1)
            }
        }
    }
    fun onResultData(data: Intent){
        val imageBitmap = data.extras?.get("data") as? Bitmap
        imageBitmap?.let{ resource->
            updateConfirm(resource)
            return
        }
        data.data?.let { galleryImgUri ->
            galleryImgUri.getBitmap(repo.pagePresenter.activity)?.let { updateConfirm(it) }
        }
    }

    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.pageAppViewModel.event.observe(owner) { evt ->
            val evt = evt ?: return@observe
            when (evt.type) {
                PageEventType.OnActivityForResult -> {
                    if (requestId == evt.hashId) {
                        val data = evt.data as? ActivityResult
                        data?.data?.let { onResultData(it) }
                    }
                }
                else -> {}
            }
        }
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }

}