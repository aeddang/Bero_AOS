package com.ironraft.pupping.bero.koin

import com.ironraft.pupping.bero.MainActivity
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.lib.page.PagePresenter
import com.lib.page.PageComposePresenter
import com.lib.page.PageAppViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val pageModelModule = module {
    singleOf(::ActivityModel)
    singleOf(::PageAppViewModel)
    singleOf(::PageComposePresenter){ bind<PagePresenter>() }
    scope<MainActivity>(){

    }
}

