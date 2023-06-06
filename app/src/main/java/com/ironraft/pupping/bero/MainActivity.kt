package com.ironraft.pupping.bero
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.core.content.ContextCompat
import com.lib.page.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.ironraft.pupping.bero.activityui.*
import com.ironraft.pupping.bero.store.DeepLinkManager
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.RepositoryEvent
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.lib.model.IwillGo
import com.lib.util.AppUtil
import com.lib.util.DataLog
import com.lib.util.PageLog
import com.skeleton.sns.SnsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
@OptIn(ExperimentalMaterialApi::class)
class MainActivity : PageComposeable() {
    //lateinit val appObserver: PageAppObserver
    lateinit var repository: PageRepository
    lateinit var pageModel: ActivityModel
    lateinit var pagePresenter: PagePresenter
    lateinit var snsManager: SnsManager
    lateinit var appSceneObserver:AppSceneObserver
    lateinit var deepLinkManager: DeepLinkManager
    private val appTag = javaClass.simpleName
    private val scope = PageCoroutineScope()

    override fun getPageActivityPresenter(): PageComposePresenter = get()
    override fun getPageActivityViewModel(): PageAppViewModel = get()
    override fun getPageActivityModel(): PageModel = get()
    /*
        val model:ActivityModel = get()
        return model
    }
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        scope.createJob()
        AppUtil.getApplicationSignature(this)
        super.onCreate(savedInstanceState)
    }

    override fun setPageScreen() {
        pageModel = get()
        snsManager = get()
        repository = get()
        pagePresenter = get()
        appSceneObserver = get()
        deepLinkManager = get()
        snsManager.setup(this)
        repository.setDefaultLifecycleOwner(this)
        deepLinkManager.setDefaultLifecycleOwner(this)
        setupComposeScreen()
        setupObserver()
    }

    override fun isGobackAble(prevPage: PageObject?, nextPage: PageObject?): Boolean {
        if(selectState?.isVisible == true) {
            this.appSceneObserver.select.value = ActivitSelectEvent(ActivitSelectType.Cancel)
            return false
        }
        if(radioState?.isVisible == true) {
            this.appSceneObserver.radio.value = ActivitRadioEvent(ActivitRadioType.Cancel)
            return false
        }
        if(sheetState?.isVisible == true) {
            this.appSceneObserver.sheet.value = ActivitSheetEvent(ActivitSheetType.Cancel)
            return false
        }
        if(this.appSceneObserver.isAlertShow ) {
            this.appSceneObserver.alert.value = ActivitAlertEvent(ActivitAlertType.Cancel)
            return false
        }
        return super.isGobackAble(prevPage, nextPage)
    }
    private var radioState: ModalBottomSheetState? = null
    private var selectState: ModalBottomSheetState? = null
    private var sheetState: ModalBottomSheetState? = null

    @OptIn(ExperimentalAnimationApi::class)
    private fun setupComposeScreen(){
        setContent {
            val radioState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                confirmValueChange = { it != ModalBottomSheetValue.Expanded},
                skipHalfExpanded = true
            )
            val selectState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                confirmValueChange = { it != ModalBottomSheetValue.Expanded},
                skipHalfExpanded = true
            )
            val sheetState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                confirmValueChange = { it != ModalBottomSheetValue.Expanded},
                skipHalfExpanded = true
            )
            val pageNv = rememberAnimatedNavController()
            this.navController = pageNv
            this.radioState = radioState
            this.selectState = selectState
            this.sheetState = sheetState
            scope.run {
                PageApp(pageNv, radioState = radioState, selectState = selectState, sheetState = sheetState)
            }
        }
        scope.launch {
            delay(100)
            repository.autoSnsLogin()
        }
    }

    var isInit = false
    var isLaunching = false
    private fun setupObserver (){
        repository.event.observe(this){evt ->
            when (evt){
                RepositoryEvent.LoginUpdated ->
                    if ( !onStoreInit() ) onPageInit()
                else -> {}
            }
        }

        appSceneObserver.event.observe(this) { event ->
            event?.let { evt ->
                when (evt.type) {
                    SceneEventType.Initate -> onPageInit()
                    else -> {}
                }
            }
        }
    }
    private fun onStoreInit():Boolean{
        if ( SystemEnvironment.firstLaunch && !isLaunching) {
            pagePresenter.changePage(
                PageProvider.getPageObject(PageID.Intro)
            )
            return true
        }
        return false
    }
    private fun onPageInit(){
        isLaunching = true
        requsetNotificationPermission()
        PageLog.d("onPageInit", appTag)
        if (!repository.isLogin) {
            isInit = false
            if (pagePresenter.currentPage?.pageID != PageID.Login.value) {
                pagePresenter.changePage(
                    PageProvider.getPageObject(PageID.Login)
                )
            }
            return
        }
        if (isInit && pagePresenter.currentPage?.pageID != PageID.Login.value) {
            PageLog.d("onPageInit already init", appTag)
            return
        }
        isInit = true

        AppObserver.pageApns.value?.let {apns ->
            if (!appObserverMove(apns.page)) {
                pagePresenter.changePage(
                    PageProvider.getPageObject(PageID.Walk)
                )
                appSceneObserver.alert.value = ActivitAlertEvent(
                    ActivitAlertType.RecivedApns, apns = apns
                )
            }
            AppObserver.pageApns.value = null
            return
        }
        pagePresenter.changePage(
            PageProvider.getPageObject(PageID.Walk)
        )
    }
    private fun appObserverMove(iwg:IwillGo? = null) : Boolean {
        iwg?.let {iwg ->
            val apnsPage = PageProvider.getPageObject(iwg)
            apnsPage?.let { page->
                if (page.isHome) pagePresenter.changePage(page)
                else pagePresenter.openPopup(page)
                return page.isHome
            }
        }
        return false
    }

    override fun onWillChangePage(nextPage: PageObject?) {
        super.onWillChangePage(nextPage)
        val page = nextPage ?: return
        val useBottom =  (activityModel as? ActivityModel)?.useBottomTabPage(page.pageID)
        appSceneObserver.useBottom.value = useBottom
        when(page.pageID){
            PageID.Walk.value -> repository.walkManager.updateSimpleView(false)
            else -> repository.walkManager.updateSimpleView(true)
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (currentPage != null) {
            deepLinkManager.changeActivityIntent(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.destoryJob()
        repository.disposeDefaultLifecycleOwner(this)
        repository.disposeLifecycleOwner(this)
        deepLinkManager.disposeDefaultLifecycleOwner(this)
        deepLinkManager.disposeLifecycleOwner(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        snsManager.onActivityResult( requestCode, resultCode, data, activityRequstId)
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun requestToken(){
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                DataLog.d("Fetching FCM registration token failed" + task.exception, appTag)
                return@OnCompleteListener
            }
            val token = task.result
            DataLog.d("Fetching FCM registration token -> $token", appTag)
            AppObserver.pushToken.value = token
        })
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            requestToken()
        } else {
            DataLog.d("FCM SDK requestPermissionLauncher denied.", tag = appTag)
        }
    }

    private fun requsetNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {

                DataLog.d("FCM SDK (and your app) can post notifications.", tag = appTag)
                requestToken()

            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                DataLog.d("FCM SDK (and your app) can not post notifications.", tag = appTag)
            } else {
                // Directly ask for the permission
                DataLog.d("FCM SDK (and your app) request.", tag = appTag)
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            requestToken()
        }
    }
}

