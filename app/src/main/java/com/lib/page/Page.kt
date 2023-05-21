package com.lib.page
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.skeleton.module.Repository
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

enum class PageAnimationType {
    None, Vertical, Horizontal, Opacity, ReverseVertical, ReverseHorizontal;
    companion object {
        const val duration:Int  = 200
    }

    val enter : EnterTransition?
        get() = when(this) {
            Vertical -> slideInVertically (tween(duration), initialOffsetY = {it/2})
            Horizontal -> slideInHorizontally (tween(duration), initialOffsetX = {it/3})
            Opacity -> fadeIn(animationSpec = tween(duration))
            ReverseVertical -> slideInVertically (tween(duration), initialOffsetY = {-it/2})
            ReverseHorizontal -> slideInHorizontally (tween(duration), initialOffsetX = {-it/3})
            None -> null
        }

    val exit : ExitTransition?
        get() = when(this) {
            Vertical -> slideOutVertically (tween(duration), targetOffsetY  = {it/2})
            Horizontal -> slideOutHorizontally (tween(duration), targetOffsetX = {it/3})
            Opacity -> fadeOut(animationSpec = tween(duration))
            ReverseVertical -> slideOutVertically (tween(duration), targetOffsetY = {-it/2})
            ReverseHorizontal -> slideOutHorizontally (tween(duration), targetOffsetX = {-it/3})
            None -> null
        }
}
data class PageObject(
    val pageID:String = "",
    var pageIDX:Int = 0,
    val key:String = UUID.randomUUID().toString()
){
    var params:HashMap<String, Any?>? = null
    var isPopup = false ; internal set
    var isHome = false
    var isHistory = true
    var isGoBackAble = true
    var animationType:PageAnimationType = PageAnimationType.Opacity
    val screenID:String get() { return "$pageID$pageIDX"}

    var isInit = false

    fun addParam(key:String, value:Any?):PageObject{
        value ?: return this
        if (params == null) {
            params  = HashMap<String, Any?>()
        }
        params!![key] = value
        return this
    }

    fun getParamValue(key:String):Any?{
        if (params == null) return null
        return params!![key]
    }
     fun copy():PageObject{
         val copyPage = PageObject(pageID, pageIDX)
         copyPage.isHome = isHome
         copyPage.isPopup = isPopup
         copyPage.params = params
         copyPage.isInit = false
         copyPage.animationType = animationType
         return copyPage
     }
}

interface PagePresenter {
    var isFullScreen:Boolean
    var systemBarColor:Int
    var appTheme:Int
    var activity:PageComposeable
    val currentPage:PageObject?
    val currentTopPage:PageObject?
    val lastPage:PageObject?
    val prevPage:PageObject?

    fun goBack(pageObject:PageObject?=null): PagePresenter
    fun clearPageHistory(pageObject:PageObject?=null): PagePresenter
    fun closePopup(key:String?): PagePresenter
    fun closePopupId(id:String?): PagePresenter
    fun closePopup(pageObject:PageObject): PagePresenter
    fun closeAllPopup(): PagePresenter
    fun openPopup(pageObject:PageObject): PagePresenter
    fun pageStart(pageObject:PageObject): PagePresenter
    fun changePage(pageObject:PageObject): PagePresenter
    fun hasPermissions( permissions: Array<out String> ): Pair< Boolean, List<Boolean>>?
    fun requestPermission( permissions: Array<out String>, requester:PageRequestPermission )
    fun loading(isRock:Boolean = false): PagePresenter
    fun loaded(): PagePresenter
    fun finishApp()
    fun superBackPressAction()
    fun findPage(pageID:String?): PageObject?
}

interface PageModel {
    var isPageInit:Boolean
    var currentPageObject:PageObject?
    @StringRes
    fun getPageExitMessage(): Int
    fun isHistoryPage( page:PageObject ): Boolean = true
    fun isFullScreenPage( page:PageObject ): Boolean = false
    fun getPageOrientation( page:PageObject ): Int
    fun getCloseExceptions(): List<String> = listOf()
}


interface PageLifecycleUser{
    fun setDefaultLifecycleOwner(owner: LifecycleOwner){}
    fun disposeDefaultLifecycleOwner(owner: LifecycleOwner){}
    fun disposeLifecycleOwner(owner: LifecycleOwner){}
}



class PageCoroutineScope : CoroutineScope {

    lateinit var job: Job
    fun createJob(){
        job = SupervisorJob()
    }
    fun destoryJob(){
        cancelJob()
        cancel()
    }
    fun cancelJob(){
        job.cancel()
    }
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    val coroutineContextIO: CoroutineContext
        get() = job + Dispatchers.IO
}

interface PageRequestPermission {
    fun onRequestPermissionResult(resultAll:Boolean ,  permissions: List<Boolean>?){}
}




