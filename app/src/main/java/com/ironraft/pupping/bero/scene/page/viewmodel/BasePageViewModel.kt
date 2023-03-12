package com.ironraft.pupping.bero.scene.page.viewmodel

import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.lib.page.*
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.PageRepository


open class BasePageViewModel(repo: PageRepository) : ViewModel(), PageViewModel {
    override val repository: PageRepository = repo
    override val observable: PageAppViewModel = repo.pagePresenter.observable
    override val presenter:PagePresenter = repo.pagePresenter
    val dataProvider : DataProvider = repo.dataProvider
    var owner: LifecycleOwner? = null; protected set

    @CallSuper
    override fun onCreateView(owner: LifecycleOwner, pageObject: PageObject?) {
        this.owner = owner
        this.repository.clearEvent()
    }

    @CallSuper
    override fun onDestroyView(owner: LifecycleOwner , pageObject: PageObject?) {
        if(this.owner != owner) return
        this.repository.disposeLifecycleOwner(owner)
        this.owner = null
        onDestroyOwner(owner, pageObject)
    }

    protected open fun onDestroyOwner(owner: LifecycleOwner , pageObject: PageObject?) {}

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        this.owner = null
    }


}