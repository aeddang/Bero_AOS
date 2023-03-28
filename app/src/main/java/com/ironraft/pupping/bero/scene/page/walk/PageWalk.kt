package com.ironraft.pupping.bero.scene.page.walk

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.lib.page.*
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.databinding.*
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.skeleton.sns.*

import javax.inject.Inject
/*
@AndroidEntryPoint
class PageWalk : PageFragment(), PageRequestPermission{
    private val appTag = javaClass.simpleName
    @Inject lateinit var repository: PageRepository
    @Inject lateinit var snsManager: SnsManager
    @Inject lateinit var pagePresenter: PagePresenter
    @Inject lateinit var dataProvider: DataProvider
    private lateinit var binding: PageWalkBinding
    override fun onViewBinding(): View {
        binding = PageWalkBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playMap.initializaMap(view, savedInstanceState)
    }
    override fun onTransactionCompleted() {
        super.onTransactionCompleted()
        pagePresenter.requestPermission(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION), this)
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionResult(resultAll: Boolean, permissions: List<Boolean>?) {
        super.onRequestPermissionResult(resultAll, permissions)
        binding.playMap.onRequestPermissionResult(resultAll, permissions)
        binding.playMap.moveMe(17.0f)
    }
    override fun onCoroutineScope() {
        super.onCoroutineScope()
        val ctx = context
        ctx ?: return
    }
}

 */