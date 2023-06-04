package com.ironraft.pupping.bero.scene.page.walk.model

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.store.PageRepository
import com.lib.page.ComponentViewModel
import com.lib.page.PageEventType
import com.lib.page.PageRequestPermission
import com.lib.util.AppUtil
import com.lib.util.getBitmap
import java.util.*


open class WalkPickViewModel(val repo: PageRepository):ComponentViewModel() {
    val requestId:Int = UUID.randomUUID().hashCode()
    val pickImage = MutableLiveData<Bitmap?>()
    fun initSetup(owner: LifecycleOwner): WalkPickViewModel {
        setDefaultLifecycleOwner(owner)
        return this
    }

    private var pickUri:Uri? = null
    fun onPick(){
        repo.pagePresenter.requestPermission(
            arrayOf(Manifest.permission.CAMERA),
            requester = object : PageRequestPermission {
                override fun onRequestPermissionResult(
                    resultAll: Boolean,
                    permissions: List<Boolean>?
                ) {
                    if (!resultAll) return
                    pickUri = AppUtil.getPickImgUri(repo.pagePresenter.activity)
                    AppUtil.openIntentImagePick(repo.pagePresenter.activity, true, requestId, fileUri = pickUri)
                }
            }
        )
    }



    private fun onResultData(data: Intent?){
        pickUri?.let {uri->
            uri.getBitmap(repo.pagePresenter.activity)?.let {
                pickImage.value = it
                return
            }
        }
        val imageBitmap = data?.extras?.get("data") as? Bitmap
        imageBitmap?.let{ resource->
            pickImage.value = resource
            return
        }
        data?.data?.let { galleryImgUri ->
            galleryImgUri.getBitmap(repo.pagePresenter.activity)?.let {
                pickImage.value = it
            }
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
                        onResultData(data?.data)
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