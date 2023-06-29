package com.ironraft.pupping.bero.scene.component.viewmodel

import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.AlbumListItemData
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.lib.page.ComponentViewModel
import com.lib.util.showCustomToast
import java.util.*

open class AlbumFunctionViewModel(val repo: PageRepository, id:String = "") :ComponentViewModel() {

    var currentId:String = id
    var data:AlbumListItemData? = null

    fun initSetup(owner: LifecycleOwner): AlbumFunctionViewModel {
        setDefaultLifecycleOwner(owner)
        return this
    }
    fun lazySetup(data: AlbumListItemData?): AlbumFunctionViewModel {
        data?.let {
            currentId = it.pictureId.toString()
            this.data = it
        }
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
                ApiType.UpdateAlbumPicturesExpose -> {
                    val isExpose = res.requestData as? Boolean ?: return@observe
                    this.data?.updata(isExpose=isExpose)
                    Toast(activity).showCustomToast(
                        activity.getString(
                            if(isExpose) R.string.alert_exposed else R.string.alert_unExposed),
                        activity
                    )
                }
                ApiType.UpdateAlbumPicturesLike -> {
                    val isLike = res.requestData as? Boolean ?: return@observe
                    this.data?.updata(isLike=isLike)
                }
                else ->{}
            }
        }

    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }
    fun updateLike(isLike:Boolean){
        val q = ApiQ(
            currentId,
            ApiType.UpdateAlbumPicturesLike,
            contentID = currentId,
            requestData = isLike)
        repo.dataProvider.requestData(q)
    }

    fun updateExpose(isExpose:Boolean){
        val q = ApiQ(
            currentId,
            ApiType.UpdateAlbumPicturesExpose,
            contentID = currentId,
            requestData = isExpose)
        repo.dataProvider.requestData(q)
    }

}