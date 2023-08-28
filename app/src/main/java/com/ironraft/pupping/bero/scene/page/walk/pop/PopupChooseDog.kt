package com.ironraft.pupping.bero.scene.page.walk.pop

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.PetProfileCheckItem
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get
import kotlinx.coroutines.launch




@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PopupChooseDog(
    viewModel: PageWalkViewModel,
    close:() -> Unit
) {
    val walkManager: WalkManager = get()
    val dataProvider: DataProvider = get()

    fun onCheck():Boolean{
        return dataProvider.user.pets.find { it.isWith } != null
    }
    var isSelect by remember { mutableStateOf( onCheck() )}
    fun onStartWalk(){
        walkManager.requestWalk()
        close()
    }

    AppTheme {
        Column(
            Modifier.wrapContentSize().padding(DimenMargin.regular.dp),
            verticalArrangement = Arrangement.spacedBy(DimenMargin.medium.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Column(
                Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.tinyExtra.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    stringResource(id = R.string.walkStartChooseDogTitle),
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize.medium.sp,
                    color = ColorApp.black
                )
                Text(
                    stringResource(id = R.string.walkStartChooseDogText),
                    fontSize = FontSize.thin.sp,
                    color = ColorApp.gray400
                )
            }
            Column(
                Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
                horizontalAlignment = Alignment.Start
            ) {
                dataProvider.user.pets.forEach{ pet->
                    PetProfileCheckItem(profile = pet){
                        isSelect = onCheck()
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = DimenMargin.tinyExtra.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FillButton(
                    modifier = Modifier.weight(1.0f),
                    type = FillButtonType.Fill,
                    text = stringResource(id = R.string.cancel),
                    color = ColorApp.gray200,
                    textColor = ColorApp.gray400
                ) {
                    close()
                }
                FillButton(
                    modifier = Modifier.weight(1.0f),
                    type = FillButtonType.Fill,
                    text = stringResource(id = R.string.button_startWalking),
                    color = ColorBrand.primary,
                    isActive = isSelect
                ) {
                    if (!isSelect) return@FillButton
                    onStartWalk()
                }
            }
        }
    }
}
