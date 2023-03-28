package com.ironraft.pupping.bero
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.lib.page.*

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
    override fun getPageActivityModel(): PageModel  {
        val model:ActivityModel = get()
        return model
    }
    override fun getPageActivityViewModel(): PageAppViewModel = get()

    override fun setPageScreen() {
        setContent {
            val nv = rememberNavController()
            this.navController = nv
            run { PageApp(nv) }
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
}

