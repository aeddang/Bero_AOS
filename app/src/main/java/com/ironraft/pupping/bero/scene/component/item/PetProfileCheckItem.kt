package com.ironraft.pupping.bero.scene.component.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.util.toAge
import com.skeleton.component.item.profile.*
import com.lib.util.toggle
@Composable
fun PetProfileCheckItem(
    modifier: Modifier = Modifier,
    profile:PetProfile,
    checked:(Boolean) -> Unit
){
    var check by remember { mutableStateOf(profile.isWith) }
    HorizontalProfile(
        modifier = modifier,
        type = HorizontalProfileType.Pet,
        funcType = if(check) HorizontalProfileFuncType.Check else HorizontalProfileFuncType.UnCheck,
        imagePath = profile.imagePath.value,
        image = profile.image.value,
        name = profile.name.value,
        gender = profile.gender.value,
        isNeutralized = profile.isNeutralized.value,
        age = profile.birth.value?.toAge(),
        breed = profile.breed.value,
    ){
        check = check.toggle()
        profile.isWith = check
        checked(check)
    }
}

