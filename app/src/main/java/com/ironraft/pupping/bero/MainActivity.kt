package com.ironraft.pupping.bero
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.lib.page.*

import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.store.PageRepository
import com.skeleton.sns.SnsManager


class MainActivity : AppCompatActivity(), Page, PageRequestPermission, PageDelegate {
    lateinit var repository: PageRepository
    lateinit var pageModel: ActivityModel
    lateinit var pagePresenter: PagePresenter
    lateinit var snsManager: SnsManager

    override fun getLayoutResID() = R.layout.activity_main
    private val appTag = javaClass.simpleName
    private val scope = PageCoroutineScope()
    private var isInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            run { PageApp() }
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

    override fun onAddedPage(pageObject: PageObject) {
        TODO("Not yet implemented")
    }

    override fun onRemovedPage(pageObject: PageObject) {
        TODO("Not yet implemented")
    }

    override fun onBottomPage(pageObject: PageObject) {
        TODO("Not yet implemented")
    }

    override fun onTopPage(pageObject: PageObject) {
        TODO("Not yet implemented")
    }

    override fun onEvent(pageObject: PageObject, type: String, data: Any?) {
        TODO("Not yet implemented")
    }

}

