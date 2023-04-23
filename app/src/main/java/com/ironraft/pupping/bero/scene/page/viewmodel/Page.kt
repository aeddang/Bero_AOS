package com.ironraft.pupping.bero.scene.page.viewmodel

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
    Intro("intro", ),
    Login("login", ),
    Walk("walk", 100),
    Explore("explore", 200),
    Chat("chat", 300),
    My("my", 400)
}