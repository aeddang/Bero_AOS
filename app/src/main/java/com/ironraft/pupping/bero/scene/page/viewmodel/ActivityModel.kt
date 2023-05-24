package com.ironraft.pupping.bero.scene.page.viewmodel

import android.content.pm.ActivityInfo
import com.lib.page.PageModel
import com.lib.page.PageObject
import com.ironraft.pupping.bero.R



class ActivityModel : PageModel{
    override var isPageInit: Boolean = false
    override var currentPageObject: PageObject? = null
    override fun getPageExitMessage(): Int  = R.string.noticeAppExit

    override fun isFullScreenPage(page: PageObject): Boolean {
        return when(page.pageID){
            PageID.PictureViewer.value -> true
            else -> false
        }
    }

    override fun getPageOrientation(page: PageObject): Int {
        return when(page.pageID){
            PageID.PictureViewer.value -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    override fun getCloseExceptions(): List<String> = arrayListOf()

    private val disableHistoryPages = arrayOf(
        PageID.Intro, PageID.AddDog, PageID.AddDogCompleted, PageID.PictureViewer).map { it.value }
    override fun isHistoryPage(page: PageObject): Boolean {
        val f= disableHistoryPages.indexOf(page.pageID)
        return f == -1
    }

    private val useBottomTabPages = arrayOf(PageID.Walk, PageID.Explore, PageID.Chat, PageID.My).map { it.value }
    fun useBottomTabPage(pageValue:String): Boolean {
        val f= useBottomTabPages.indexOf(pageValue)
        return f != -1
    }

}