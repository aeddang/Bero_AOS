package com.lib.page

import android.location.Location
import com.google.android.gms.tasks.Task


open class PageComposePresenter : PagePresenter{
    override lateinit var activity: PageComposeable
    override var isFullScreen: Boolean
        get() = activity.isFullScreen
        set(value) {
            activity.isFullScreen = value
        }
    override var isKeepScreen: Boolean
        get() = activity.isKeepScreen
        set(value) {
            activity.isKeepScreen = value
        }
    override var systemBarColor:Int
        get() = activity.systemBarColor
        set(value) {
            activity.systemBarColor = value
        }

    override var appTheme:Int
        get() = activity.appTheme
        set(value) {
            activity.appTheme = value
        }

    override val currentPage: PageObject?
        get() = activity.currentPage
    override val currentTopPage: PageObject?
        get() = activity.currentTopPage
    override val lastPage: PageObject?
        get() = activity.lastPage
    override val prevPage: PageObject?
        get() = activity.prevPage


    override fun goBack(pageObject: PageObject?): PagePresenter {
        activity.goBack(pageObject)
        return this
    }

    override fun clearPageHistory(pageObject: PageObject?): PagePresenter {
        activity.clearPageHistory(pageObject)
        return this
    }

    override fun closePopup(key: String?): PagePresenter {
        key?.let { activity.closePopup(it) }
        return this
    }
    override fun closePopupId(id: String?): PagePresenter {
        id?.let { activity.onClosePopupId(it) }
        return this
    }
    override fun closePopup(pageObject: PageObject): PagePresenter {
        activity.closePopup(pageObject)
        return this
    }

    override fun closeAllPopup(): PagePresenter {
        activity.closeAllPopup()
        return this
    }

    override fun openPopup(pageObject: PageObject): PagePresenter {
        activity.openPopup(pageObject)
        return this
    }

    override fun pageStart(pageObject: PageObject): PagePresenter {
        activity.pageStart(pageObject)
        return this
    }

    override fun changePage(pageObject: PageObject): PagePresenter {
        activity.pageChange(pageObject)
        return this
    }

    override fun hasPermissions(permissions: Array<out String>) = activity.hasPermissions(permissions)

    override fun requestPermission(
        permissions: Array<out String>,
        requester: PageRequestPermission
    ) {
        activity.requestPermission(permissions,requester)
    }

    override fun loading(isRock: Boolean): PagePresenter {
        activity.loading(isRock)
        return this
    }

    override fun loaded(): PagePresenter {
        activity.loaded()
        return this
    }

    override fun finishApp() {
        activity.finishApp()
    }

    override fun superBackPressAction(){
        activity.superBackPressAction()
    }

    override fun findPage(pageID: String?): PageObject? {
        val id = pageID ?: return null
        return activity.findPage(id)
    }

}