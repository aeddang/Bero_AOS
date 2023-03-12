package com.skeleton.module
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ironraft.pupping.bero.store.PageRepository


class ViewModelFactory(private val repository: PageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T
            = modelClass.getConstructor(PageRepository::class.java).newInstance(repository)
    
}