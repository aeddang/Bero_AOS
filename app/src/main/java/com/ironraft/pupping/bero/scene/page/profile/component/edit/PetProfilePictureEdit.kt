package com.ironraft.pupping.bero.scene.page.profile.component.edit

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSelectEvent
import com.ironraft.pupping.bero.activityui.ActivitSelectType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.*
import com.lib.util.AppUtil
import com.lib.util.getBitmap
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.theme.*
import dev.burnoo.cokoin.get
import java.util.*


@Composable
fun PetProfilePictureEdit(
    modifier: Modifier = Modifier,
    profile:PetProfile
) {
    val appTag = "PetProfilePictureEdit"
    val appSceneObserver: AppSceneObserver = get()
    val pagePresenter: PagePresenter = get()
    val activityModel: PageAppViewModel = get()
    val dataProvider: DataProvider = get()
    val image by profile.image.observeAsState()
    val imagePath by profile.imagePath.observeAsState()

    val requestId:Int  by remember {mutableStateOf(UUID.randomUUID().hashCode())}
    fun onEdit(img:Bitmap = BitmapFactory.decodeResource(pagePresenter.activity.resources, R.drawable.profile_dog_default)){
        val q = ApiQ(appTag,
            ApiType.UpdatePetImage,
            contentID = profile.petId.toString(),
            requestData = img)
        dataProvider.requestData(q)
    }
    fun onResultData(data: Intent){
        val imageBitmap = data.extras?.get("data") as? Bitmap
        imageBitmap?.let{ resource->
            onEdit(resource)
            return
        }
        data.data?.let { galleryImgUri ->
            galleryImgUri.getBitmap(pagePresenter.activity)?.let {
                onEdit(it)
            }
        }
    }

    fun onPick(){
        appSceneObserver.select.value = ActivitSelectEvent(
            type = ActivitSelectType.ImgPicker
        ){ select ->
            if (select == -1) return@ActivitSelectEvent
            when(select){
                0 -> {
                    AppUtil.openIntentImagePick(pagePresenter.activity, false, requestId)
                }
                1 ->{
                    pagePresenter.requestPermission(
                        arrayOf(Manifest.permission.CAMERA),
                        requester = object : PageRequestPermission {
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
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(DimenProfile.heavy.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            ProfileImage(
                image = image,
                imagePath = imagePath,
                isSelected = true,
                size = DimenProfile.heavy,
                emptyImagePath = R.drawable.profile_dog_default,
                onEdit = {
                    onPick()
                },
                onDelete = {
                    if (imagePath == null && image == null){
                        onPick()
                    } else {
                        val ac = pagePresenter.activity
                        appSceneObserver.sheet.value = ActivitSheetEvent(
                            type = ActivitSheetType.Select,
                            title = ac.getString(R.string.alert_profileDeleteConfirm),
                            text = ac.getString(R.string.alert_profileDeleteConfirmText),
                            buttons = arrayListOf(
                                ac.getString(R.string.cancel),
                                ac.getString(R.string.button_delete)
                            )
                        ) { select ->
                            if (select == 1) onEdit()
                        }
                    }
                }
            )

        }
    }
}

@Preview
@Composable
fun PetProfilePictureEditComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PetProfilePictureEdit(
            profile = PetProfile()
        )
    }
}