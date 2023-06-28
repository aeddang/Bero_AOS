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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import com.lib.util.PageLog
import com.lib.util.resetScrollState
import com.lib.util.showCustomToast
import java.util.*
import kotlin.math.abs
import kotlin.system.exitProcess

abstract class PageComposeable : AppCompatActivity(), PageRequestPermission {
    companion object {
        var active = false
    }

    private val appTag = javaClass.simpleName
    abstract fun getPageActivityPresenter(): PageComposePresenter
    abstract fun getPageActivityViewModel(): PageAppViewModel
    abstract fun getPageActivityModel(): PageModel
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
    open fun loading(isRock: Boolean = false){
        activityViewModel.isLoading.value = true
        activityViewModel.isLock.value = isRock
    }
    open fun loaded(){
        activityViewModel.isLoading.value = false
        activityViewModel.isLock.value = false
    }


    protected lateinit var activityViewModel : PageAppViewModel
    protected lateinit var activityModel : PageModel
    protected var navController: NavHostController? = null
    protected val historys = Stack<PageObject>  ()
    protected val popups = ArrayList<PageObject>()

    private var currentRequestPermissions = HashMap<Int, PageRequestPermission>()

    protected var currentPageObject: PageObject? = null
    val currentPage: PageObject?
        get(){
            return currentPageObject
        }
    val currentTopPage: PageObject?
        get(){
            return if( popups.isEmpty() ) currentPageObject else popups.last()
        }
    val lastPage: PageObject?
        get(){
            return if( popups.isEmpty() ) currentPageObject else popups.last()
        }
    val prevPage: PageObject?
        get(){
            return if( historys.isEmpty() ) null else historys.last()
        }

    fun findPage(pageID:String): PageObject?{
        if( currentPageObject?.pageID == pageID ) return currentPageObject
        return popups.findLast { it.pageID == pageID }
    }


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

    var isKeepScreen:Boolean = false
        set(value) {
            if( value == field ) return
            field = value
            if(field){
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }else{
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
        currentPageObject = null
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
    open fun goBack(pageObject: PageObject? = null){
        //if(pageObject != null) clearPageHistory(pageObject)
        this.onBackPressed()
    }

    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>; private set
    protected var activityRequstId:Int = -1
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
        if(popups.isNotEmpty()){
            val last = popups.last()
            if (!last.isGoBackAble) return onGoBackPage()
            if (!isGobackAble(last)) return
            if( last.isHome ) onExitAction()
            onClosePopup(last)
            return
        }
        currentPageObject?.let { page ->
            if (!page.isGoBackAble) return onGoBackPage()
            if (!isGobackAble(page)) return
            if( page.isHome ) onExitAction()
            else onBackPressedAction()
        }

    }

    private var finalExitActionTime:Long = 0L
    private fun resetBackPressedAction() { finalExitActionTime = 0L }
    protected open fun onExitAction() {
        //super.onBackPressedDispatcher.onBackPressed()
        val cTime =  Date().time
        if( abs(cTime - finalExitActionTime) < 3000L ) { exitProcess(-1) }
        else {
            finalExitActionTime = cTime
            Toast(this).showCustomToast(activityModel.getPageExitMessage(), this)
        }
    }

    protected open fun onBackPressedAction() {
        if( historys.isEmpty()) {
            onExitAction()
        }else {
            onPageChange(historys.pop()!!, isBack = true)
        }
    }

    /*
    Page Transaction
    */
    @CallSuper
    protected open fun onWillChangePage(nextPage: PageObject?){
        nextPage ?: return
        nextPage.isInit = false
        isFullScreen = activityModel.isFullScreenPage(nextPage)
        val willChangeOrientation = activityModel.getPageOrientation(nextPage)
        if (willChangeOrientation != -1 && requestedOrientation != willChangeOrientation) requestedOrientation = willChangeOrientation
        activityViewModel.event.value = PageEvent(
            PageEventType.WillChangePage,
            nextPage.pageID,
            nextPage
        )
        activityViewModel.currentTopPage.value = nextPage
    }

    @CallSuper
    protected open fun onChangedPage(){
        currentTopPage?.let {
            activityViewModel.event.value = PageEvent(
                PageEventType.ChangedPage,
                it.pageID,
                it
            )
        }
    }

    private fun onGoBackPage(){
        currentTopPage?.let {
            activityViewModel.event.value = PageEvent(
                PageEventType.GoBack,
                it.pageID,
                it
            )
        }
    }

    private fun onPageChange(
        pageObject: PageObject,
        isBack: Boolean = false
    ) {
        PageLog.d("onPageChange -> $pageObject", tag = this.appTag)
        if( !isChangePageAble(pageObject) ) return
        if( currentTopPage?.pageID == pageObject.pageID ) {
            if(pageObject.params == null){
                PageLog.d("onPageChange -> reload", tag = this.appTag)
                activityViewModel.event.value = PageEvent(
                    PageEventType.ReloadPage,
                    pageObject.pageID,
                    pageObject
                )
                return
            }else{
                val currentValues = currentPageObject?.params?.map { it.toString() }
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
        //onCloseAllPopup()
        //resetBackPressedAction()
        val top = currentTopPage
        val prev = currentPageObject
        pageObject.isPopup = false
        currentPageObject = pageObject
        onWillChangePage(pageObject)
        if (isBack) {
            navController?.popBackStack()
        } else {
            resetScrollState(pageObject.pageID)
            navController?.navigate(pageObject.pageID){
                top?.let { top->
                    val isHistory = activityModel.isHistoryPage(top)
                    if(!isHistory) {
                        popUpTo(top.pageID) {inclusive = true }
                    }
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
        onChangedPage()
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
        val top = currentTopPage
        resetBackPressedAction()
        pageObject.isPopup = true
        resetScrollState(pageObject.pageID)
        popups.add(pageObject)
        onWillChangePage(pageObject)
        navController?.navigate(pageObject.pageID) {
            top?.let { top->
                val isHistory = activityModel.isHistoryPage(top)
                if(!isHistory) {
                    popUpTo(top.pageID) {inclusive = true }
                }
            }
        }
        onChangedPage()
        PageLog.d("onOpenPopup -> ${pageObject.pageID} completed", tag = this.appTag)
    }
    private fun onCloseAllPopup() {
        val allPopups = popups.map { it }
        popups.clear()
        onWillChangePage(currentPageObject)
        allPopups.forEach { p ->
            navController?.popBackStack()
        }
        onChangedPage()
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
        val oldPopups = popups
        popups.remove(pageObject)
        val newPopups = popups
        val nextPage = if(newPopups.isNotEmpty()) newPopups.last() else currentPageObject
        onWillChangePage(nextPage)
        navController?.popBackStack() //route = pageObject.pageID, inclusive = true
        onChangedPage()
    }

    /*
    Permission
    */
    open fun hasPermissions(permissions: Array<out String>): Pair<Boolean, List<Boolean>>? {
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
        hasPermissions(permissions)?.let { requestPermissionResult(requestCode, it.first, it.second) }
    }

    override fun onStart() {
        super.onStart()
        PageComposeable.active = true
    }

    override fun onStop() {
        super.onStop()
        PageComposeable.active = false
    }

}