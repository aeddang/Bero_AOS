package com.ironraft.pupping.bero.scene.page.viewmodel
import com.lib.page.PageProvider
import com.lib.page.PageObject
import com.lib.page.PageViewFragment
import com.ironraft.pupping.bero.scene.page.intro.PageIntro
import com.ironraft.pupping.bero.scene.page.login.PageLogin
import com.ironraft.pupping.bero.scene.page.walk.PageWalk


class FragmentProvider : PageProvider{
    fun getPageObject(pageID: PageID) :PageObject {
        val obj = PageObject(pageID.value, pageID.position)
        when(pageID){
           else -> {}
        }
        return obj
    }
    fun getPageTitle(pageID: PageID) :String{
        return when(pageID){
            else -> "Page Title"
        }
    }

    override fun getPageView(pageObject: PageObject): PageViewFragment {
        return when(pageObject.pageID){
            PageID.Intro.value -> PageIntro()
            PageID.Login.value -> PageLogin()
            PageID.Walk.value -> PageWalk()
            PageID.My.value -> PageLogin()
            else -> PageIntro()
        }
    }
}

enum class PageID(val value: String, val position: Int = 9999){
    Intro("Intro", 1),
    Login("Login", 999),
    Walk("Walk", 100),
    Matching("Matching", 200),
    Diary("Diary", 300),
    My("My", 400)
}


