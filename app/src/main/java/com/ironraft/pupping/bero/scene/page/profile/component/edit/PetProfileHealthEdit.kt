package com.ironraft.pupping.bero.scene.page.profile.component.edit

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
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
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.activityui.ActivitSelectEvent
import com.ironraft.pupping.bero.activityui.ActivitSelectType
import com.ironraft.pupping.bero.activityui.ActivitSheetEvent
import com.ironraft.pupping.bero.activityui.ActivitSheetType
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.AlbumData
import com.ironraft.pupping.bero.store.api.rest.CodeCategory
import com.ironraft.pupping.bero.store.api.rest.CodeData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.provider.model.ModifyPetProfileData
import com.ironraft.pupping.bero.store.provider.model.ModifyUserProfileData
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.UserProfile
import com.lib.page.*
import com.lib.util.AppUtil
import com.lib.util.ComponentLog
import com.lib.util.getBitmap
import com.lib.util.toAge
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.component.item.profile.ProfileImage
import com.skeleton.sns.SnsUser
import com.skeleton.theme.*
import com.skeleton.view.button.SelectButton
import com.skeleton.view.button.SelectButtonType
import dev.burnoo.cokoin.get
import java.util.*


@Composable
fun PetProfileHealthEdit(
    modifier: Modifier = Modifier,
    profile:PetProfile
) {
    val appTag = "PetProfileHealthEdit"
    val dataProvider:DataProvider = get()
    val viewModel: ComponentViewModel by remember { mutableStateOf(ComponentViewModel()) }

    val immunStatus by profile.immunStatus.observeAsState()
    val weight by profile.weight.observeAsState()
    val size by profile.size.observeAsState()
    val animalId by profile.animalId.observeAsState()
    val microchip by profile.microchip.observeAsState()
    fun getCodeData():Boolean{
        val params = HashMap<String, String>()
        params[ApiField.category] = CodeCategory.Personality.name.lowercase()
        val q = ApiQ(appTag, ApiType.GetCode, query = params, requestData = CodeCategory.Status)
        dataProvider.requestData(q)
        return true
    }

    val isInit by remember { mutableStateOf(getCodeData()) }
    val codes:HashMap<String,String> by remember { mutableStateOf(HashMap<String, String>()) }
    val apiResult = dataProvider.result.observeAsState()

    @Suppress("UNCHECKED_CAST")
    apiResult.value?.let { res ->
        if(!viewModel.isValidResult(res)) return@let
        if (res.requestData != CodeCategory.Status) return@let
        when ( res.type ){
            ApiType.GetCode -> {
                (res.data as? List<CodeData>)?.let { datas ->
                    datas.forEach {
                        it.id?.toString()?.let { key ->
                            codes[key] = it.value ?: ""
                        }
                    }
                }
            }
            else ->{}
        }
    }

    AppTheme {
        Column (
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DimenMargin.regular.dp),
        ) {
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.weight),
                text = if(weight != null) weight.toString()+ stringResource(id = R.string.kg) else "-" ,
                useStroke = false,
                useMargin = false
            ){

            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.grey50)
            )
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.height),
                text = if(size != null) size.toString()+ stringResource(id = R.string.cm) else "-" ,
                useStroke = false,
                useMargin = false
            ){

            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.grey50)
            )
            if(codes.isNotEmpty())
                SelectButton(
                    type = SelectButtonType.Medium,
                    title = stringResource(id = R.string.immunization),
                    text =  PetProfile
                        .exchangeListToString(
                            PetProfile.exchangeStringToList(immunStatus).map{ codes[it] ?: "" }),
                    useStroke = false,
                    useMargin = false
                ){

                }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.grey50)
            )
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.animalId),
                text = if(animalId != null) animalId!!  else stringResource(id = R.string.button_unregistered) ,
                useStroke = false,
                useMargin = false
            ){

            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(DimenLine.light.dp)
                .background(ColorApp.grey50)
            )
            SelectButton(
                type = SelectButtonType.Medium,
                title = stringResource(id = R.string.microchip),
                text = if(microchip != null) microchip!!  else stringResource(id = R.string.button_unregistered) ,
                useStroke = false,
                useMargin = false
            ){

            }
        }
    }
}

@Preview
@Composable
fun PetProfileHealthEditComposePreview(){
    Column (
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PetProfileHealthEdit(
            profile = PetProfile()
        )
    }
}