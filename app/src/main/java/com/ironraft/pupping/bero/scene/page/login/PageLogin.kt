package com.ironraft.pupping.bero.scene.page.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.login.LoginFragment
import com.lib.page.*
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.databinding.*
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.skeleton.component.dialog.Alert
import com.skeleton.sns.*
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject

@AndroidEntryPoint
class PageLogin : PageFragment() {
    private val appTag = javaClass.simpleName
    @Inject lateinit var repository: PageRepository
    @Inject lateinit var snsManager: SnsManager
    @Inject lateinit var pagePresenter: PagePresenter
    @Inject lateinit var dataProvider: DataProvider
    private lateinit var binding: PageLoginBinding
    override fun onViewBinding(): View {
        binding = PageLoginBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = snsManager.fb.callbackManager
        binding.buttonFb.registerCallback(callback,snsManager.fb)
        binding.buttonLoginFb.setOnClickListener{
            binding.buttonFb.callOnClick()
        }
        binding.buttonLoginGoogle.setOnClickListener {
            snsManager.requestLogin(SnsType.Google)
        }
    }
    override fun onCoroutineScope() {
        super.onCoroutineScope()
        val ctx = context
        ctx ?: return

        snsManager.error.observe(this){err->
            err ?: return@observe
            when(err.event){
                SnsEvent.Login -> {
                    Alert.Builder(ctx)
                        .setText( ctx.resources.getString(R.string.alertSnsLoginError))
                        .show()
                }
                else ->{}
            }
        }

        snsManager.user.observe(this) { user ->
            user ?: return@observe
            snsManager.getUserInfo()
        }

        snsManager.userInfo.observe(this) { userInfo ->
            userInfo ?: return@observe
            snsManager.user.value?.let {
                repository.registerSnsLogin(it, userInfo)
            }
        }
    }


}