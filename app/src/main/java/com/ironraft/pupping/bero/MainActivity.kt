package com.ironraft.pupping.bero
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import com.lib.page.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.store.PageRepository
import com.skeleton.sns.SnsManager
import org.koin.android.ext.android.get


class MainActivity : PageComposeable() {
    lateinit var repository: PageRepository
    lateinit var pageModel: ActivityModel
    lateinit var pagePresenter: PagePresenter
    lateinit var snsManager: SnsManager
    private val appTag = javaClass.simpleName
    private val scope = PageCoroutineScope()
    private var isInit = false
    override fun getPageActivityPresenter(): PageComposePresenter = get()
    override fun getPageActivityViewModel(): PageAppViewModel = get()
    override fun getPageActivityModel(): PageModel{
        val model:ActivityModel = get()
        return model
    }


    @OptIn(ExperimentalAnimationApi::class)
    override fun setPageScreen() {
        pageModel = get()
        snsManager = get()
        repository = get()
        snsManager.setup(this)
        setContent {
            val pageNv = rememberAnimatedNavController()
            this.navController = pageNv
            run { PageApp(pageNv) }
            this.repository.setDefaultLifecycleOwner(this)
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //if (currentPage != null) { deepLinkManager.changeActivityIntent(intent) }
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
        //deepLinkManager.disposeDefaultLifecycleOwner(this)
        //deepLinkManager.disposeLifecycleOwner(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        snsManager.onActivityResult(requestCode, resultCode, data)
    }
}

