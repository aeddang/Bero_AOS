package com.ironraft.pupping.bero.store

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID


data class Shareable (
    val pageID:PageID = PageID.Intro,
    var params:HashMap<String,Any?>? = null,
    val text:String? = null,
    var image:String? = null,
    val isPopup:Boolean = true,
    val shareImage:Bitmap? = null
)

class ShareManager {
    val share = MutableLiveData<Shareable?>()
}