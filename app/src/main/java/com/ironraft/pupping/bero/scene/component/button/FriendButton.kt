package com.ironraft.pupping.bero.scene.component.button

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageComposePresenter
import com.lib.util.replace
import com.lib.util.showCustomToast
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get

enum class FriendButtonType {
    Fill, Icon
}
enum class FriendButtonFuncType {
    Request, Requested, Accept, Reject, Delete, Chat, Move;

    @get:DrawableRes
    val icon: Int
        get() = when (this) {
            Request -> R.drawable.add_friend
            Requested -> R.drawable.add_friend
            Delete -> R.drawable.remove_friend
            Chat -> R.drawable.chat
            Accept -> R.drawable.check
            Reject -> R.drawable.close
            Move -> R.drawable.search
        }

    val bgColor: Color
        get() = when (this) {
            Move, Accept, Request, Chat -> ColorBrand.primary
            Delete, Reject -> ColorApp.black
            Requested -> ColorApp.grey300
        }

    val iconColor: Color
        get() = when (this) {
            Move, Accept, Request, Chat -> ColorBrand.primary
            Delete, Reject -> ColorApp.black
            Requested -> ColorApp.grey300
        }

    val textColor: Color
        get() = when (this) {
            Move -> ColorBrand.primary
            else -> ColorApp.white
        }

    @get:StringRes
    val text: Int
        get() = when (this) {
            Request -> R.string.button_addFriend
            Requested -> R.string.button_requestFriend
            Delete -> R.string.button_remove
            Chat -> R.string.button_chat
            Accept -> R.string.button_accept
            Reject -> R.string.button_reject
            Move -> R.string.button_viewProfile
        }

    val buttonType: FillButtonType
        get() = when (this) {
            Delete, Move -> FillButtonType.Stroke
            else -> FillButtonType.Fill
        }
}

@Composable
fun FriendButton(
    type:FriendButtonType = FriendButtonType.Fill,
    userId:String? = null,
    userName:String? = null,
    funcType:FriendButtonFuncType,
    size:Float = DimenButton.mediumExtra,
    radius:Float = DimenRadius.thin,
    textSize:Float = FontSize.light
) {
    val appTag = "FriendButton"

    val pagePresenter:PageComposePresenter = get()
    val appSceneObserver:AppSceneObserver = get()
    val dataProvider:DataProvider = get()


    fun action(){
       val id = userId ?: return
       if (id == dataProvider.user.snsUser?.snsID) {return}
       when (funcType) {

           FriendButtonFuncType.Request -> {
               val q = ApiQ(appTag, ApiType.RequestFriend,  contentID = id)
               dataProvider.requestData(q)
           }
           FriendButtonFuncType.Requested -> {
               Toast(pagePresenter.activity).showCustomToast(
                   "Already friend (write the phrase)",
                   pagePresenter.activity
               )
           }
           FriendButtonFuncType.Delete -> {
               val ac = pagePresenter.activity
               appSceneObserver.sheet.value = ActivitSheetEvent(
                   type = ActivitSheetType.Select,
                   title =  ac.getString(R.string.alert_friendDeleteConfirm).replace(userName ?: ""),
                   text = ac.getString(R.string.alert_friendDeleteConfirmText),
                   buttons = arrayListOf(ac.getString(R.string.cancel), ac.getString(R.string.button_removeFriend))
               ){
                   if (it == 1) {
                       val q = ApiQ(appTag, ApiType.DeleteFriend,  contentID = id)
                       dataProvider.requestData(q)
                   }
               }
           }
           FriendButtonFuncType.Chat -> {
               //self.appSceneObserver.event = .sendChat(userId: self.userId ?? "")
           }
           FriendButtonFuncType.Accept -> {
               val q = ApiQ(appTag, ApiType.AcceptFriend,  contentID = id)
               dataProvider.requestData(q)
           }
           FriendButtonFuncType.Reject -> {
               val q = ApiQ(appTag, ApiType.RejectFriend,  contentID = id)
               dataProvider.requestData(q)
           }
           FriendButtonFuncType.Move ->{
               /*
               PageProvider.getPageObject(.user)
               .addParam(key: .id, value:id)
               */
           }


        }

    }
    AppTheme {
        Box (
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ){
            when (type){
                FriendButtonType.Fill ->
                    FillButton(
                        type = funcType.buttonType,
                        icon = funcType.icon,
                        isOriginIcon = false,
                        text = stringResource(id = funcType.text),
                        size = size,
                        radius = radius,
                        color = funcType.bgColor,
                        textColor = funcType.textColor,
                        textSize = textSize
                    ){
                        action()
                    }
                FriendButtonType.Icon ->
                    CircleButton(
                        type = CircleButtonType.Icon,
                        icon = funcType.icon,
                        isSelected = true,
                        strokeWidth = DimenStroke.regular,
                        activeColor = funcType.iconColor
                    ){
                        action()
                    }
            }
        }
    }
}

@Preview
@Composable
fun FriendButtonComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FriendButton(
            funcType = FriendButtonFuncType.Request
        )
        FriendButton(
            type = FriendButtonType.Icon,
            funcType = FriendButtonFuncType.Request
        )
    }

}