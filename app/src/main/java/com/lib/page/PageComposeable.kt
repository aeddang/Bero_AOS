package com.lib.page
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.*
import android.net.ConnectivityManager.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import com.lib.util.PageLog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

abstract class PageComposeable : AppCompatActivity(), PageRequestPermission {
    private val appTag = javaClass.simpleName
    abstract fun getPageActivityPresenter(): PageComposePresenter
    abstract fun getPageActivityModel(): PageModel
    abstract fun getPageActivityViewModel(): PageAppViewModel
    abstract fun setPageScreen()
    protected open fun isGobackAble(prevPage:PageObject? = null, nextPage:PageObject? = null):Boolean = true
    protected open fun isChangePageAble(pageObject: PageObject):Boolean = true
    /*
    Animation
     */
    @AnimRes protected open fun getPageStart(): Int { return android.R.anim.fade_in }
    @AnimRes protected open fun getPageIn(pageID: String): Int { return android.R.anim.fade_in}
    @AnimRes protected open fun getPageOut(pageID: String): Int { return android.R.anim.fade_out }
    @AnimRes protected open fun getPopupIn(pageID: String): Int { return android.R.anim.fade_in }
    @AnimRes protected open fun getPopupOut(pageID: String): Int { return android.R.anim.fade_out }

    @CallSuper
    open fun finishApp(){ super.finish() }
    open fun loading(isRock: Boolean = false){}
    open fun loaded(){}


    protected lateinit var activityModel : PageModel
    protected lateinit var activityViewModel : PageAppViewModel
    protected var navController: NavHostController? = null
    protected val historys = Stack<PageObject>  ()
    protected val popups = ArrayList<PageObject>()

    private var currentRequestPermissions = HashMap<Int, PageRequestPermission>()

    val currentPage: PageObject?
        get(){
            return activityModel.currentPageObject
        }
    val currentTopPage: PageObject?
        get(){
            return if( popups.isEmpty() ) currentPage else popups.last()
        }
    val lastPage: PageObject?
        get(){
            return if( popups.isEmpty() ) currentPage else popups.last()
        }
    val prevPage: PageObject?
        get(){
            return if( historys.isEmpty() ) null else historys.last()
        }
    val hasLayerPopup: Boolean
        get() = popups.find { it.isTop } != null


    @Suppress("DEPRECATION")
    var isFullScreen:Boolean = false
        set(value) {
            if( value == field ) return
            field = value
            if(field){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.hide(WindowInsets.Type.systemBars())
                } else {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.show(WindowInsets.Type.systemBars())
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)
                }

            }
        }

    @ColorRes
    var systemBarColor:Int = -1
        set(value) {
            if( value == -1 ) return
            field = value
            val c = this.applicationContext.getColor(value)
            window.navigationBarColor = c
            window.statusBarColor = c
        }

    @StyleRes
    var appTheme:Int = -1
        set(value) {
            if( value == -1 ) return
            field = value
            application.setTheme(value)
        }

    @SuppressLint("MissingPermission")
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityResult()
        onCreatedView()
        val builder = NetworkRequest.Builder()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), object : NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                activityViewModel.networkStatus.postValue(PageNetworkStatus.Available)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                activityViewModel.networkStatus.postValue(PageNetworkStatus.Lost)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                activityViewModel.networkStatus.postValue(PageNetworkStatus.Lost)
            }
        })
        activityViewModel.networkStatus.value = if( connectivityManager.isDefaultNetworkActive ) PageNetworkStatus.Available else PageNetworkStatus.Lost
    }


    @CallSuper
    protected open fun onCreatedView(){
        activityModel = getPageActivityModel()
        activityViewModel = getPageActivityViewModel()
        getPageActivityPresenter().activity = this
        setPageScreen()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        popups.clear()
        historys.clear()
        activityModel.currentPageObject = null
        currentRequestPermissions.clear()
    }


    @CallSuper
    open fun clearPageHistory(pageObject: PageObject? = null){
        if(pageObject == null) {
            historys.clear()
            return
        }
        var peek:PageObject? = null
        do {
            if(peek != null) historys.pop()
            peek = try { historys.peek() }catch (e: EmptyStackException){ null }
        } while (pageObject != peek  && !historys.isEmpty())
    }
    @CallSuper
    open fun openPopup(pageObject: PageObject) {
        onOpenPopup(pageObject)
    }
    @CallSuper
    open fun closePopup(pageObject: PageObject) {
        onClosePopup(pageObject)
    }
    @CallSuper
    open fun closePopup(key: String) {
        onClosePopup(key)
    }
    @CallSuper
    open fun closeAllPopup(){
        onCloseAllPopup()
    }
    @CallSuper
    open fun pageInit() {
        activityModel.isPageInit = true

    }
    @CallSuper
    open fun pageStart(pageObject: PageObject){
        onPageChange(pageObject, true)
    }
    @CallSuper
    open fun pageChange(
        pageObject: PageObject
    ){
        onPageChange(pageObject, false)
    }
    @CallSuper
    open fun goHome(idx: Int = 0){
        pageChange(activityModel.getHome(idx))
    }
    @CallSuper
    open fun goBack(pageObject: PageObject? = null){
        if(pageObject != null) clearPageHistory(pageObject)
        this.onBackPressed()
    }


    @Suppress("DEPRECATION")
    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityViewModel.event.value = PageEvent(
            PageEventType.OnActivityForResult,
            data = data,
            hashId = activityRequstId
        )
    }
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>; private set
    private var activityRequstId:Int = -1
    private fun setupActivityResult(){
        startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            activityViewModel.event.value = PageEvent(
                PageEventType.OnActivityForResult,
                data = result,
                hashId = activityRequstId
            )
            activityRequstId = -1
        }
    }
    fun registActivityResult(intent:Intent, id:Int = -1){
        activityRequstId = id
        startActivityForResult.launch(intent)
    }

    /*
    BackPressed
    */
    fun superBackPressAction(){
        super.onBackPressedDispatcher.onBackPressed()
    }
    @CallSuper
    override fun onBackPressed() {
        val closeablePopups = popups.filter { !it.isLayer }
        if(closeablePopups.isNotEmpty()){
            val last = closeablePopups.last()
            if (!isGobackAble(last)) return
            popups.remove(last)
            onClosePopup(last)
            return
        }
        activityModel.currentPageObject ?: return
        if (!isGobackAble(activityModel.currentPageObject)) return
        if( activityModel.isHomePage(activityModel.currentPageObject!!) ) onExitAction()
        else onBackPressedAction()
    }

    private var finalExitActionTime:Long = 0L
    private fun resetBackPressedAction() { finalExitActionTime = 0L }
    protected open fun onExitAction() {
        super.onBackPressedDispatcher.onBackPressed()
        /*
        val cTime =  Date().time
        if( abs(cTime - finalExitActionTime) < 3000L ) { exitProcess(-1) }
        else {
            finalExitActionTime = cTime
            Toast(this).showCustomToast(activityModel.getPageExitMessage(), this)
        }*/
    }

    protected open fun onBackPressedAction() {
        if( historys.isEmpty()) {
            if( activityModel.currentPageObject == null) goHome()
            else onExitAction()
        }else {
            onPageChange(historys.pop()!!, false)
        }
    }

    /*
    Page Transaction
    */
    @CallSuper
    protected open fun onWillChangePage(prevPage: PageObject?, nextPage: PageObject?){
        nextPage ?: return
        isFullScreen = activityModel.isFullScreenPage(nextPage)
        val willChangeOrientation = activityModel.getPageOrientation(nextPage)
        if (willChangeOrientation != -1 && requestedOrientation != willChangeOrientation) requestedOrientation = willChangeOrientation
        activityViewModel.event.value = PageEvent(
            PageEventType.WillChangePage,
            nextPage.pageID,
            nextPage
        )
    }

    private fun onPageChange(
        pageObject: PageObject,
        isStart: Boolean = false,
        isBack: Boolean = false
    ) {
        PageLog.d("onPageChange -> $pageObject", tag = this.appTag)
        if( !isChangePageAble(pageObject) ) return
        if( activityModel.currentPageObject?.pageID == pageObject.pageID ) {

            if(pageObject.params == null){
                PageLog.d("onPageChange -> reload", tag = this.appTag)
                activityViewModel.event.value = PageEvent(
                    PageEventType.ReloadPage,
                    pageObject.pageID,
                    pageObject
                )
                return
            }else{
                val currentValues = activityModel.currentPageObject?.params?.map { it.toString() }
                val values = pageObject.params?.map { it.toString() }
                if (currentValues == values) {
                    PageLog.d("onPageChange -> reload", tag = this.appTag)
                    activityViewModel.event.value = PageEvent(
                        PageEventType.ReloadPage,
                        pageObject.pageID,
                        pageObject
                    )
                    return
                }
            }
        }
        onCloseAllPopup()
        resetBackPressedAction()
        val prev = activityModel.currentPageObject
        pageObject.isPopup = false
        activityModel.currentPageObject = pageObject
        onWillChangePage(prev, pageObject)
        if (isBack) {
            navController?.popBackStack()
        } else {
            navController?.navigate(pageObject.pageID){
                anim {
                    enter = if (isStart) getPageStart() else getPageIn(pageObject.pageID)
                    exit = getPageOut(pageObject.pageID)
                }
            }
        }
        if( !isBack ) {
            prev?.let {
                if( activityModel.isHistoryPage(it) ) historys.push(it)
            }
        }
        if(prev == null) activityViewModel.event.value = PageEvent(
            PageEventType.Init,
            pageObject.pageID,
            pageObject.params
        )
        activityViewModel.event.value = PageEvent(
            PageEventType.ChangePage,
            pageObject.pageID,
            pageObject.params
        )

        PageLog.d("onPageChange -> ${pageObject.pageID} completed", tag = this.appTag)
    }
    private var finalAddedPopupID:String? = null
    private var finalOpenPopupTime:Long = 0L
    private fun onOpenPopup(
        pageObject: PageObject
    ) {
        if( !isChangePageAble(pageObject) ) return
        val cTime =  Date().time
        if( finalAddedPopupID == pageObject.pageID && (abs(cTime - finalOpenPopupTime) < 500 ) ) return
        finalAddedPopupID = pageObject.pageID
        finalOpenPopupTime = cTime
        resetBackPressedAction()
        pageObject.isPopup = true
        popups.add(pageObject)
        navController?.navigate(pageObject.pageID) {
            anim {
                enter = getPopupIn(pageObject.pageID)
                exit = getPopupOut(pageObject.pageID)
            }
        }
        activityViewModel.event.value = PageEvent(
            PageEventType.AddPopup,
            pageObject.pageID,
            pageObject.params
        )
        PageLog.d("onOpenPopup -> ${pageObject.pageID} completed", tag = this.appTag)
    }
    private fun onCloseAllPopup() {
        val allPopups = popups.map { it }
        popups.clear()
        allPopups.forEach { p ->
            navController?.popBackStack()
            activityViewModel.event.value = PageEvent(
                PageEventType.RemovePopup,
                p.pageID,
                p.params
            )
        }
        onWillChangePage(null, activityModel.currentPageObject)
    }
    fun onClosePopupId(id: String){
        val f = popups.find { it.pageID == id }
        f?.let {
            onClosePopup(it)
        }
    }
    private fun onClosePopup(key: String){
        val f = popups.find { it.key == key }
        f?.let {
            onClosePopup(it)
        }
    }
    private fun onClosePopup(pageObject: PageObject){
        popups.remove(pageObject)
        val nextPage = if(popups.isNotEmpty()) popups.last() else activityModel.currentPageObject
        onWillChangePage(null, nextPage)
        navController?.popBackStack()
        activityViewModel.event.value = PageEvent(
            PageEventType.RemovePopup,
            pageObject.pageID,
            pageObject.params
        )
    }

    /*
    Permission
    */
    open fun hasPermissions(permissions: Array<out String>): Pair<Boolean, List<Boolean>>? {
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return null
        val permissionResults = ArrayList<Boolean>()
        var resultAll = true
        for (permission in permissions) {
            val grant =  checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            permissionResults.add(grant)
            if( !grant ) resultAll = false
        }
        return Pair(resultAll, permissionResults)
    }

    open fun requestPermission(permissions: Array<out String>, requester: PageRequestPermission)
    {
        val grantResult = currentRequestPermissions.size
        currentRequestPermissions[grantResult] = requester
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { requestPermissionResult(grantResult, true); return }
        hasPermissions(permissions)?.let {
            if ( !it.first ) requestPermissions(permissions, grantResult) else requestPermissionResult(
                grantResult,
                true
            )
        }
    }
    private fun requestPermissionResult(
        requestCode: Int,
        resultAll: Boolean,
        permissions: List<Boolean>? = null
    )
    {
        currentRequestPermissions[requestCode]?.onRequestPermissionResult(resultAll, permissions)
        currentRequestPermissions.remove(requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        hasPermissions(permissions)?.let { requestPermissionResult(requestCode, it.first, it.second) }
    }



}