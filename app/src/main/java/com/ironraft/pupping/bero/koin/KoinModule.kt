package com.ironraft.pupping.bero.koin
import com.ironraft.pupping.bero.PageAppObserver
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.preference.StoragePreference
import com.ironraft.pupping.bero.store.api.ApiInterceptor
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiManager
import com.ironraft.pupping.bero.store.Topic
import com.ironraft.pupping.bero.store.database.ApiCoreDataManager
import com.ironraft.pupping.bero.store.DeepLinkManager
import com.lib.page.*
import com.lib.observer.LocationObserver
import com.skeleton.sns.SnsManager
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.skeleton.module.network.NetworkFactory
import com.skeleton.module.firebase.Analytics
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val pageModelModule = module {
    singleOf(::PageAppObserver) { bind<AppObserver>() }
    singleOf(::ActivityModel) { bind<PageModel>() }
    singleOf(::Analytics)
    singleOf(::PageAppViewModel)
    singleOf(::StoragePreference)
    singleOf(::DataProvider)
    singleOf(::ApiCoreDataManager)
    singleOf(::NetworkFactory)
    singleOf(::ApiInterceptor)
    singleOf(::AppSceneObserver)
    singleOf(::ApiManager)
    singleOf(::Topic)
    singleOf(::PageComposePresenter) { bind<PagePresenter>() } //인터페이스 지정 필요시
    singleOf(::SnsManager)
    singleOf(::LocationObserver)
    singleOf(::WalkManager)
    singleOf(::PageRepository)
    singleOf(::DeepLinkManager)
    //
    //viewModelOf(::BasePageViewModel)
    //scope<MainActivity>(){} 싱글 activity 확장시 사용
}

