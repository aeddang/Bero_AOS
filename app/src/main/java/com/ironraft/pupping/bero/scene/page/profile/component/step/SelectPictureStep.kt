package com.ironraft.pupping.bero.scene.page.profile.component.step

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.profile.PageAddDogStep
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.lib.page.*
import com.lib.util.AppUtil
import com.lib.util.getBitmap
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get
import java.util.UUID

@Composable
fun SelectPictureStep(
    profile:ModifyPetProfileData?,
    step:PageAddDogStep,
    prev: () -> Unit,
    next: (ModifyPetProfileData) -> Unit
) {
    val appTag = "SelectPictureStep"
    val pagePresenter:PageComposePresenter = get()
    val activityModel: PageAppViewModel = get()

    fun getPrevData() : Bitmap?{
        return when (step){
            PageAddDogStep.Picture -> profile?.image
            else -> null
        }
    }
    val requestId:Int  by remember {mutableStateOf(UUID.randomUUID().hashCode())}
    var selectData:Bitmap?  by remember { mutableStateOf(getPrevData()) }

    fun onAction(){
        if (selectData == null) return
        when (step){
            PageAddDogStep.Picture -> next(ModifyPetProfileData(image = selectData))
            else -> {}
        }
    }

    fun onResultData(data: Intent){
        val imageBitmap = data.extras?.get("data") as? Bitmap
        imageBitmap?.let{ resource->
            selectData = resource
            return
        }
        data.data?.let { galleryImgUri ->
            galleryImgUri.getBitmap(pagePresenter.activity)?.let {
                selectData = it
            }
        }
    }

    val pageEvent = activityModel.event.observeAsState()
    pageEvent.value?.let { evt ->
        when(evt.type) {
            PageEventType.OnActivityForResult -> {
                if (requestId == evt.hashId){
                    val data = evt.data as? ActivityResult
                    data?.data?.let { onResultData(it) }
                }
            }
            else -> {}
        }
    }

    AppTheme {
        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage(
                image = selectData,
                isSelected = selectData != null,
                size = DimenProfile.heavy,
                emptyImagePath = R.drawable.profile_dog_default,
                modifier = Modifier.padding(bottom = DimenMargin.regular.dp)
            )
            SelectButton(
                type = SelectButtonType.Small,
                icon = R.drawable.album,
                text = stringResource(id = R.string.button_selectAlbum)
            ) {
                AppUtil.openIntentImagePick(pagePresenter.activity, false, requestId)
            }
            SelectButton(
                type = SelectButtonType.Small,
                icon = R.drawable.add_photo,
                text = stringResource(id = R.string.button_takeCamera)
            ) {
                pagePresenter.requestPermission(
                    arrayOf(Manifest.permission.CAMERA),
                    requester = object : PageRequestPermission{
                        override fun onRequestPermissionResult(
                            resultAll: Boolean,
                            permissions: List<Boolean>?
                        ) {
                            if (!resultAll) return
                            AppUtil.openIntentImagePick(pagePresenter.activity, true, requestId)
                        }
                    }
                )

            }
            Spacer(modifier = Modifier.weight(1.0f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tinyExtra.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!step.isFirst) {
                    FillButton(
                        modifier = Modifier.weight(1.0f),
                        type = FillButtonType.Fill,
                        text = stringResource(id = R.string.button_prev),
                        color = ColorApp.black
                    ) {
                        prev()
                    }
                }
                FillButton(
                    modifier = Modifier.weight(1.0f),
                    type = FillButtonType.Fill,
                    text = stringResource(id = R.string.button_next),
                    color = ColorBrand.primary
                ) {
                    onAction()
                }

            }
        }
    }
}


@Preview
@Composable
fun SelectPictureStepComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectPictureStep(
            profile = ModifyPetProfileData(),
            step = PageAddDogStep.Name,
            next = {

            },
            prev = {

            }
        )
    }

}