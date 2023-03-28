package com.skeleton.sns

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ironraft.pupping.bero.R
import com.lib.page.PageComposeable
import com.lib.util.Log
import java.util.*


class GoogleSignManager : Sns{

    private val appTag = javaClass.simpleName

    val respond = MutableLiveData<SnsResponds?>()
    val error = MutableLiveData<SnsError?>()
    val type = SnsType.Google
    var googleSignInClient: GoogleSignInClient? = null; private set
    var pageActivity:PageComposeable? = null; private set
    val requestCode:Int = UUID.randomUUID().hashCode()
    init {}

    fun setup(ac:PageComposeable){
        pageActivity = ac
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(ac.getString(R.string.web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(ac, gso)
    }

    override fun destroy(){
        pageActivity = null
        googleSignInClient = null
    }

    override fun getAccessTokenInfo() {

    }

    override fun getUserInfo() {
        pageActivity?.let { ac ->
            val acct = GoogleSignIn.getLastSignedInAccount(ac)
            if (acct != null) {
                val personName = acct.displayName
                //val personGivenName = acct.givenName
                //val personFamilyName = acct.familyName
                val personEmail = acct.email
                //val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl

                val profile = SnsUserInfo(
                    personName,
                    personPhoto?.path,
                    personEmail
                )
                respond.value = SnsResponds(SnsEvent.GetProfile, type, profile)
            } else {
                error.value = SnsError(SnsEvent.GetProfile, type)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if ( requestCode != this.requestCode  ) return false
        data?.let { data ->
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        return true
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.

            account.idToken?.let { token ->
                val user = SnsUser(
                    type,
                    account.id ?: "",
                    token
                )
                respond.value = SnsResponds(SnsEvent.Login, type, user)
            }

        } catch (e: ApiException) {
            Log.e(appTag, "signInResult:failed code=" + e.statusCode)
            error.value = SnsError(SnsEvent.Login, type, e)
        }
    }

    override fun requestUnlink() {
        Log.e(appTag, "Not supported")
        googleSignInClient?.revokeAccess()
    }
    override fun requestLogin() {
        Log.d(appTag, "requestLogin")
        googleSignInClient?.signInIntent?.let {
            pageActivity?.registActivityResult(it, requestCode)
        }
    }

    override fun requestLogOut() {
        Log.d(appTag, "requestLogOut")

        respond.value = SnsResponds(SnsEvent.Logout, type)
        googleSignInClient?.signOut()
    }

}