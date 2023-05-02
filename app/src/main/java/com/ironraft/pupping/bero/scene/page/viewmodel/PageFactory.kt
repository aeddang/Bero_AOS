package com.ironraft.pupping.bero.scene.page.viewmodel

import com.lib.page.PageAnimationType
import com.lib.page.PageObject

data class PageError<T>(val type:T, val code:String?, val msg:String? = null, val id: String? = null)

object PageAcvityEvent {
    const val bottomTabView = "bottomTabView"
    const val bottomTabHide = "bottomTabHide"
}

object PageParam {
    const val requestCode = "requestCode"
    const val title = "title"
    const val image = "image"
    const val type = "type"
    const val id = "id"
    const val subId = "subId"
    const val idx = "idx"
    const val data = "data"
    const val datas = "datas"
}

enum class PageID(val value: String, val position: Int = 9999){
    Splash("splash"),
    Intro("intro"),
    Login("login"),
    Walk("walk", 100),
    Explore("explore", 200),
    Chat("chat", 300),
    My("my", 400),
    Privacy("privacy"),
    ServiceTerms("serviceTerms")
}

class PageProvider {
    companion object{
        fun getPageObject(pageID:PageID, animationType: PageAnimationType? = null) : PageObject {
            val pobj = PageObject(pageID.value, pageID.position )
            pobj.isHome = isHome(pageID)
            pobj.animationType = animationType ?: getType(pageID)
            return pobj
        }

        fun isHome(pageID:PageID) : Boolean{
            return when (pageID){
                PageID.Intro, PageID.Login, PageID.My, PageID.Walk, PageID.Explore, PageID.Chat  -> true
                else -> false
            }
        }

        fun getType(pageID:PageID): PageAnimationType{
            return when (pageID){
                PageID.Splash, PageID.Intro, PageID.Login, PageID.My, PageID.Walk, PageID.Explore, PageID.Chat -> PageAnimationType.None
                //PageID.Privacy, PageID.ServiceTerms -> PageAnimationType.Vertical
                else -> PageAnimationType.Horizontal
            }
        }
    }

}
