package com.ironraft.pupping.bero.scene.page.walk.pop

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.DimenRadius

enum class WalkPopupType{
    None, ChooseDog, WalkUsers, WalkUser;
    val isHalf : Boolean
        get() = when(this) {
           WalkPopupType.WalkUsers -> false
           else -> true
        }
}

data class WalkPopupData(
    val type:WalkPopupType,
    var value:Any? = null
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalkPopup(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState,
    viewModel: PageWalkViewModel,
    type: WalkPopupType = WalkPopupType.None,
    value: Any? = null,
    close:() -> Unit
) {

    AppTheme {
        ModalBottomSheetLayout(
            modifier =  modifier,
            sheetState = sheetState,
            sheetContent = {
                when (type) {
                    WalkPopupType.WalkUsers -> PopupWalkUsers( viewModel = viewModel, close = close )
                    else -> Spacer(modifier = Modifier.fillMaxWidth())
                }
            },
            sheetShape = MaterialTheme.shapes.large.copy(
                topStart = CornerSize(DimenRadius.medium.dp),
                topEnd = CornerSize(DimenRadius.medium.dp)
            ),
            sheetBackgroundColor = ColorApp.white
        ) {

        }
    }
}
