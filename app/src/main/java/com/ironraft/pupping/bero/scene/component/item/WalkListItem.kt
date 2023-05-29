package com.ironraft.pupping.bero.scene.component.item

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.skeleton.component.item.profile.*
import com.skeleton.theme.*
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.viewmodel.ReportFunctionViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.rest.MissionCategory
import com.ironraft.pupping.bero.store.api.rest.WalkData
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.ironraft.pupping.bero.store.walk.model.WalkPath
import com.skeleton.component.item.ListItem
import com.skeleton.view.button.WrapTransparentButton


class WalkListItemData{
    var index:Int = -1; private set
    var imagePath:String? = null; private set
    var type:MissionCategory = MissionCategory.All; private set
    var title:String? = null; private set
    var description:String? = null; private set
    var pets:List<PetProfile> = listOf(); private set
    var originData:WalkData? = null; private set
    var walkPath:WalkPath? = null; private set

    fun setData(data:WalkData, idx:Int, ctx:Context) : WalkListItemData{
        index = idx
        originData = data
        data.locations?.let {
            walkPath = WalkPath().setData(it)
        }
        imagePath = walkPath?.picture?.smallPictureUrl
        title = WalkManager.viewDistance(data.distance ?: 0.0) + " " + ctx.getString(R.string.walks)
        data.pets?.let { datas->
            pets = datas.mapIndexed{ idx, profile ->
                PetProfile().init(profile, index = idx)
            }
        }
        type = MissionCategory.Walk
        return this
    }
}

@Composable
fun WalkListItem(
    modifier: Modifier = Modifier,
    data:WalkListItemData,
    imgSize: Size,
    action: (() -> Unit)? = null
){

    ListItem(
        modifier = modifier,
        imagePath = data.imagePath,
        emptyImage = R.drawable.noimage_16_9,
        imgSize = imgSize,
        title = data.title,
        subTitle = data.description,
        icon = data.type.icon,
        pets = data.pets,
        iconAction = action,
        move = action
    )

}


