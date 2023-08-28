package com.skeleton.component.item.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.SystemEnvironment
import com.ironraft.pupping.bero.store.provider.model.Gender
import com.skeleton.theme.*
import com.skeleton.view.button.FillButtonType
import com.skeleton.view.button.ImageButton
import com.skeleton.view.button.TransparentButton


@Composable
fun ProfileInfoDescription(
    age:String? = null,
    breed:String? = null,
    gender:Gender? = null,
    isNeutralized:Boolean? = null,
    useCircle:Boolean = true,
    color:Color = ColorApp.gray500,
    action:(() -> Unit)? = null

) {
    AppTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = if(useCircle) DimenMargin.tiny.dp else 0.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            gender?.let { gender ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DimenMargin.microExtra.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    isNeutralized?.let {
                        Image(
                            painterResource(R.drawable.neutralized),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(if (it) ColorApp.green600 else ColorApp.gray200),
                            modifier = Modifier.size(DimenIcon.tiny.dp, DimenIcon.tiny.dp)
                        )
                    }
                    Text(
                        stringResource(id = gender.title),
                        fontSize = FontSize.thin.sp,
                        color = color
                    )
                }
            }
            age?.let{ age ->
                if (useCircle) {
                    Box(
                        modifier = Modifier
                            .size(DimenCircle.thin.dp)
                            .clip(CircleShape)
                            .background(ColorBrand.primary)
                    )
                } else {
                    Text(
                        ", ",
                        fontSize = FontSize.thin.sp,
                        color = color
                    )

                }
                Text(
                    age,
                    fontSize = FontSize.thin.sp,
                    color = color
                )
            }

            breed?.let { breed ->
                SystemEnvironment.breedCode[breed]?.let {breedValue->
                    if (useCircle) {
                        Box(
                            modifier = Modifier
                                .size(DimenCircle.thin.dp)
                                .clip(CircleShape)
                                .background(ColorBrand.primary)
                        )
                    } else {
                        Text(
                            ", ",
                            fontSize = FontSize.thin.sp,
                            color = color
                        )

                    }
                    Text(
                        breedValue,
                        fontSize = FontSize.thin.sp,
                        color = color
                    )
                }
            }

            action?.let {
                ImageButton(
                    defaultImage = R.drawable.edit,
                    size = DimenIcon.thin,
                    defaultColor = color
                ){
                    it()
                }
            }

        }

    }

}

@Preview
@Composable
fun ProfileInfoDescriptionComposePreview(){
    Column (
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileInfoDescription(
            age = "10",
            gender = Gender.Female,
            isNeutralized = true
        ){

        }
        ProfileInfoDescription(
            age = "10",
            gender = Gender.Female,
            isNeutralized = true,
            useCircle = false
        )
    }

}