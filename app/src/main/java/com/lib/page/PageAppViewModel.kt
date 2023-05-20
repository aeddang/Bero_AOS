package com.lib.page

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.lib.model.IwillGo


enum class PageEventType{
    Init,
    ReloadPage,
    WillChangePage, ChangedPage, GoBack,
    ShowKeyboard, HideKeyboard,
    OnActivityForResult,
    Event
}

enum class PageStatus{
    Free, Busy
}

enum class PageNetworkStatus{
    Available, Lost, Undefined
}

open class AppObserver {
    companion object {
        val pushToken: MutableLiveData<String?> = MutableLiveData(null)
        val pageApns: MutableLiveData<PageApns?> = MutableLiveData(null)

        fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
            pushToken.removeObservers(owner)
            pageApns.removeObservers(owner)
        }
    }
}

data class PageEvent(val type:PageEventType, val id: String = "", var data:Any? = null, val eventType:String? = null, val hashId: Int = -1)
data class PageApns(val title:String?, val text: String? , val page:IwillGo)

class PageAppViewModel {
    val event = MutableLiveData<PageEvent?>()
    val networkStatus = MutableLiveData<PageNetworkStatus>()
    val status = MutableLiveData<PageStatus>()
    val currentTopPage:MutableLiveData<PageObject?> = MutableLiveData(null)
    init {
        status.value = PageStatus.Free
        networkStatus.value = PageNetworkStatus.Undefined
    }
    fun removeObserve(owner: LifecycleOwner){
        event.removeObservers(owner)
        status.removeObservers(owner)
        networkStatus.removeObservers(owner)
        currentTopPage.removeObservers(owner)
    }
    fun onDestroyView(owner: LifecycleOwner) {
        removeObserve(owner)
    }
}