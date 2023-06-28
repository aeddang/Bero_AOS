package com.ironraft.pupping.bero.scene.page.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.lib.page.*
import com.ironraft.pupping.bero.store.PageRepository
import com.lib.util.PageLog

open class PageViewModel(val pageID:PageID, val repo: PageRepository) : ComponentViewModel() {
    var appTag:String = pageID.value; private set
    val currentPage = MutableLiveData<PageObject?>()
    val goBack = MutableLiveData<PageObject?>()
    val onReload = MutableLiveData<PageObject?>()
    val onClose = MutableLiveData<PageObject?>()
    var lifecycleOwner: LifecycleOwner? = null; private set
    fun goBackCompleted(){ goBack.value = null }
    fun onReloadCompleted(){ onReload.value = null }
    fun initSetup(owner: LifecycleOwner): PageViewModel {
        lifecycleOwner = owner
        setDefaultLifecycleOwner(owner)
        repo.pageAppViewModel.currentTopPage.observe(owner) {
            val page = it ?: return@observe
            if(currentPage.value != null) {
                currentPage.value?.let { current->
                    if (current.key != page.key) {
                        onClose.value = current
                        onClosePage()
                    }
                }
                return@observe
            }
            currentPage.value = page
            PageLog.d("currentPage key $it", appTag)
            PageLog.d(page, appTag)
        }
        repo.pageAppViewModel.event.observe(owner) {
            val evt = it ?: return@observe
            onPageEvent(evt)
            if(evt.id != appTag) return@observe
            val pageObject = evt.data as? PageObject ?: return@observe
            when(evt.type){
                PageEventType.ReloadPage -> onReload.value = pageObject
                else -> {}
            }
            if (currentPage.value?.key != pageObject.key) return@observe
            onCurrentPageEvent(evt.type, pageObject)
            when(evt.type){
                PageEventType.GoBack -> goBack.value = pageObject
                else -> {}
            }
        }
        return this
    }
    open fun onPageEvent(evt:PageEvent){}
    open fun onCurrentPageEvent(type:PageEventType, pageObj:PageObject){
        currentPage.value?.key?.let {
            PageLog.d("currentPage key $it", appTag)
            PageLog.d(type, appTag)
        }
    }
    open fun onClosePage(){
        lifecycleOwner?.let { owner->
            repo.disposeLifecycleOwner(owner)
        }
    }

    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
    }

    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }
}

