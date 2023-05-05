package com.ironraft.pupping.bero.store

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.lib.model.IwillGo
import com.lib.model.SingleLiveData
import com.lib.model.WhereverYouCanGo
import com.lib.page.PageLifecycleUser
import com.lib.page.PagePresenter
import com.lib.util.Log
import com.lib.util.showCustomToast
import com.skeleton.module.firebase.FirebaseDynamicLink


data class Shareable (
    val pageID:PageID = PageID.Intro,
    var params:HashMap<String,Any?>? = null,
    val text:String? = null,
    var image:String? = null,
    val isPopup:Boolean = true,
    val shareImage:Bitmap? = null
)

class ShareManager {
    val share = SingleLiveData<Shareable?>(null)
}

class DeepLinkManager(
    val pagePresenter: PagePresenter
) : FirebaseDynamicLink.Delegate, PageLifecycleUser

{
    val shareManager: ShareManager = ShareManager()
    private var appTag = javaClass.simpleName
    private var dynamicLink: FirebaseDynamicLink = FirebaseDynamicLink(pagePresenter.activity)

    init {
        dynamicLink.setOnDynamicLinkListener(this)
    }
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner){
        shareManager.share.observe(owner, Observer{shareable: Shareable?->
            shareable ?: return@Observer
            sendSns(
                String(),
                shareable.image,
                shareable.text,
                shareable.pageID.value,
                shareable.pageID.position,
                shareable.params,
                shareable.isPopup
            )
            shareManager.share.value = null
        })
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner){
        shareManager.share.removeObservers(owner)
    }

    fun changeActivityIntent(intent: Intent) {
        dynamicLink.initDynamicLink(intent)
    }

    override fun onDetactedDeepLink(deepLink: Uri) {
        Log.d("DeepLinkManager", "onDetactedDeepLink()  come:  ${deepLink}")
        deepLink.query?.let {
            goQueryPage(it)
        }
    }

    override fun onCreateDynamicLinkComplete(dynamicLink: String) {
        pagePresenter.loaded()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, dynamicLink)
        shareIntent.type = "text/plain"
        pagePresenter.activity.startActivity(Intent.createChooser(shareIntent, "share"))
    }

    override fun onCreateDynamicLinkError() {
        pagePresenter.loaded()
        Toast(pagePresenter.activity).showCustomToast("DynamicLinkError", pagePresenter.activity)
    }

    fun sendSns(
        title: String?,
        image: String?,
        desc: String?,
        pageID: String = PageID.Intro.value,
        pageIDX: Int = 9999,
        param: HashMap<String, Any?>? = null,
        isPopup: Boolean = false
    ) {
        val queryString = WhereverYouCanGo.stringfyQurryIwillGo(pageID, pageIDX, param, isPopup)
        dynamicLink.requestDynamicLink(title, image, desc, queryString)
    }

    fun goQueryPage(query: String?) {
        query ?: return
        val iwillGo = WhereverYouCanGo.parseQurryIwillGo(query)
        goPage(iwillGo)
    }


    fun goPage(iwillGo: IwillGo) {
        Log.d(appTag, "goPage $iwillGo")
        val pageObj = PageProvider.getPageObject(iwillGo)
        pageObj ?: return
        if (pageObj.isHome) pagePresenter.changePage(pageObj)
        else pagePresenter.openPopup(pageObj)
    }

}