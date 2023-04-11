package com.ironraft.pupping.bero.koin

import com.ironraft.pupping.bero.PageActivityViewModel
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.scene.page.viewmodel.BasePageViewModel
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.preference.StoragePreference
import com.ironraft.pupping.bero.store.api.ApiInterceptor
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiManager
import com.ironraft.pupping.bero.store.Topic
import com.ironraft.pupping.bero.store.database.DataBaseManager
import com.lib.page.PageComposePresenter
import com.lib.page.PageAppViewModel
import com.skeleton.module.network.NetworkFactory
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val pageModelModule = module {
    singleOf(::ActivityModel)
    singleOf(::PageAppViewModel)
    singleOf(::StoragePreference)
    singleOf(::DataProvider)
    singleOf(::DataBaseManager)
    singleOf(::NetworkFactory)
    singleOf(::ApiInterceptor)
    singleOf(::PageActivityViewModel)
    singleOf(::ApiManager)
    singleOf(::PageComposePresenter)//{ bind<PagePresenter>() } 인터페이스 지정 필요시
    singleOf(::Topic)
    singleOf(::PageRepository)
    //
    viewModelOf(::BasePageViewModel)
    //scope<MainActivity>(){} 싱글 activity 확장시 사용
}

