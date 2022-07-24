package com.dagger.module

import android.content.Context
import com.lib.page.PageActivityPresenter
import com.lib.page.PagePresenter
import com.ironraft.pupping.bero.scene.page.viewmodel.ActivityModel
import com.ironraft.pupping.bero.scene.page.viewmodel.FragmentProvider
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.ShareManager
import com.ironraft.pupping.bero.store.Topic
import com.ironraft.pupping.bero.store.api.ApiInterceptor
import com.ironraft.pupping.bero.store.api.ApiManager
import com.ironraft.pupping.bero.store.database.DataBaseManager
import com.ironraft.pupping.bero.store.preference.StoragePreference
import com.skeleton.module.ViewModelFactory
import com.skeleton.module.network.NetworkFactory
import com.skeleton.sns.SnsManager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoreModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext ctx: Context): Context = ctx

    @Singleton
    @Provides
    fun provideStoragePreference(ctx: Context): StoragePreference = StoragePreference(ctx)

    @Singleton
    @Provides
    fun provideNetworkFactory(@ApplicationContext ctx: Context): NetworkFactory = NetworkFactory(ctx)

    @Singleton
    @Provides
    fun provideSnsManager(@ApplicationContext ctx: Context): SnsManager = SnsManager(ctx)

    @Singleton
    @Provides
    fun provideDataBaseManager(ctx: Context, storage:StoragePreference): DataBaseManager
            = DataBaseManager(ctx, storage)

    @Singleton
    @Provides
    fun provideTopic(storage:StoragePreference, pagePresenter: PagePresenter): Topic
            = Topic(pagePresenter, storage)

    @Singleton
    @Provides
    fun provideApiManager(ctx: Context, networkFactory: NetworkFactory, interceptor: ApiInterceptor): ApiManager
            = ApiManager(ctx, networkFactory, interceptor)

    @Singleton
    @Provides
    fun providePagePresenter(): PagePresenter = PageActivityPresenter ()

    @Singleton
    @Provides
    fun provideFragmentProvider(): FragmentProvider = FragmentProvider ()

    @Singleton
    @Provides
    fun provideActivityModel(): ActivityModel = ActivityModel()

    @Singleton
    @Provides
    fun provideApiInterceptor(): ApiInterceptor = ApiInterceptor()

    @Singleton
    @Provides
    fun provideDataProvider(): DataProvider = DataProvider()

    @Singleton
    @Provides
    fun provideShareManager(): ShareManager = ShareManager()

    @Singleton
    @Provides
    fun providePageRepository(
        ctx: Context,
        storage:StoragePreference,
        dataBaseManager: DataBaseManager,
        dataProvider: DataProvider,
        apiManager:ApiManager,
        pageModel: ActivityModel,
        pageProvider: FragmentProvider,
        pagePresenter: PagePresenter,
        shareManager:ShareManager,
        snsManager: SnsManager,
        topic:Topic,
        interceptor: ApiInterceptor

    ): PageRepository = PageRepository(ctx,
        storage, dataBaseManager,
        dataProvider, apiManager,
        pageModel, pageProvider, pagePresenter,shareManager, snsManager, topic,  interceptor)

    @Singleton
    @Provides
    fun provideViewModelFactory(repository: PageRepository): ViewModelFactory = ViewModelFactory(repository)

}