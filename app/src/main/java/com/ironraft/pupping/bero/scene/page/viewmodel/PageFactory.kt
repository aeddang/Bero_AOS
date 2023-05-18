package com.ironraft.pupping.bero.scene.page.viewmodel

import com.lib.model.IwillGo
import com.lib.page.PageAnimationType
import com.lib.page.PageObject

data class PageError<T>(val type:T, val code:String?, val msg:String? = null, val id: String? = null)

object PageAcvityEvent {
    const val bottomTabView = "bottomTabView"
    const val bottomTabHide = "bottomTabHide"
}

object PageParam {
    const val idx = "idx"
    const val id = "id"
    const val subId = "subId"
    const val link = "link"
    const val userData = "userData"
    const val data = "data"
    const val datas = "datas"
    const val subData = "subData"
    const val type = "type"
    const val subType = "subType"
    const val title = "title"
    const val text = "text"
    const val subText = "subText"
    const val isFriend = "isFriend"
    const val isEdit = "isEdit"
    const val isInitAction = "isInitAction"
}

enum class PageID(val value: String, val position: Int = 9999){
    Splash("splash"),
    Intro("intro"),
    Login("login"),
    Walk("walk", 100),
    Explore("explore", 200),
    Chat("chat", 300),
    My("my", 400),
    Dog("dog"),
    Album("album"),
    Webview("webview"),
    Privacy("privacy"),
    ServiceTerms("serviceTerms"),
    AddDog("addDog"),
    AddDogCompleted("addDogCompleted")
}

class PageProvider {
    companion object{
        fun getPageObject(pageID:PageID, animationType: PageAnimationType? = null) : PageObject {
            val pobj = PageObject(pageID.value, pageID.position )
            pobj.isHome = isHome(pageID)
            pobj.isHistory = isHistory(pageID)
            pobj.animationType = animationType ?: getType(pageID)
            return pobj
        }
        fun getPageObject(iwillgo:IwillGo, animationType: PageAnimationType? = null) : PageObject? {
            PageID.values().find { it.value.equals(iwillgo.pageID) }?.let { pageID ->
                val pobj = PageObject(iwillgo.pageID, iwillgo.pageIDX )
                val isHome = isHome(pageID)
                pobj.isHome = isHome
                pobj.isPopup = !isHome
                pobj.isHistory = isHistory(pageID)
                pobj.params = iwillgo.param
                pobj.animationType = animationType ?: getType(pageID)
                return pobj
            }
            return null
        }
        fun isHome(pageID:PageID) : Boolean{
            return when (pageID){
                PageID.Intro, PageID.Login, PageID.My, PageID.Walk, PageID.Explore, PageID.Chat  -> true
                else -> false
            }
        }

        fun isHistory(pageID:PageID) : Boolean{
            return when (pageID){
                PageID.Intro, PageID.Login, PageID.AddDog, PageID.AddDogCompleted  -> false
                else -> true
            }
        }
        fun getType(pageID:PageID): PageAnimationType{
            return when (pageID){
                PageID.Splash, PageID.Intro, PageID.Login, PageID.My, PageID.Walk, PageID.Explore, PageID.Chat -> PageAnimationType.None
                PageID.Privacy, PageID.ServiceTerms, PageID.Webview-> PageAnimationType.None
                else -> PageAnimationType.Horizontal
            }
        }
    }

}
