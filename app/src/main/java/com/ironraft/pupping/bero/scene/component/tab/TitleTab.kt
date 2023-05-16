package com.ironraft.pupping.bero.scene.component.tab

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeleton.theme.*
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.lib.page.PageAppViewModel
import com.lib.page.PageComposePresenter
import com.lib.util.ComponentLog
import com.skeleton.view.button.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import org.koin.compose.koinInject

enum class TitleTabType {
    Page {
        override var margin: Float = DimenApp.pageHorinzontal
        override var marginBottom: Float = DimenMargin.thin
    },
    Section{
        override var textSize: Float = FontSize.light

    };
    open var textFamily: FontWeight = FontWeight.SemiBold
    open var textSize: Float = FontSize.bold
    open var margin: Float = 0.0f
    open var marginBottom: Float = 0.0f
}

enum class TitleTabButtonType {
    More, Add, Edit, Close, Back, Setting, AlramOn, Alarm, Block, AddAlbum,
    AddFriend, Friend, AddChat,
    ViewMore, ManageDogs;

    @get:DrawableRes
    val icon:Int?
        get() = when (this) {
            Back -> R.drawable.back
            More -> R.drawable.more_vert
            Add -> R.drawable.add
            AddAlbum -> R.drawable.album
            Edit -> null
            Close -> R.drawable.close
            Alarm -> R.drawable.notification_off
            AlramOn -> R.drawable.notification_on
            Setting -> R.drawable.settings
            ViewMore, ManageDogs -> R.drawable.direction_right
            AddFriend  -> R.drawable.add_friend
            Friend  -> R.drawable.human_friends
            AddChat  -> R.drawable.add_chat
            Block  -> R.drawable.block
        }


    @get:StringRes
    val text:Int?
        get() = when (this) {
            Edit -> R.string.button_edit
            ViewMore -> R.string.button_viewMore
            ManageDogs -> R.string.button_manageDogs
            else -> null
        }


    val color:Color
        get() = when (this) {
            Edit ->  ColorBrand.primary
            ViewMore, ManageDogs -> ColorApp.grey400
            else -> ColorApp.black
        }

}

@Composable
fun TitleTab(
    modifier: Modifier = Modifier,
    parentScrollState: ScrollState? = null,
    type:TitleTabType = TitleTabType.Page,
    title:String? = null,
    lineLimit:Int = Int.MAX_VALUE,
    alignment: TextAlign = TextAlign.Start,
    useBack:Boolean = false,
    margin:Float? = null,
    sortPetProfile:PetProfile? = null,
    sortButton:String? = null,
    sort:(() -> Unit)? = null,
    buttons:ArrayList<TitleTabButtonType> = arrayListOf(),
    icons:ArrayList<String?> = arrayListOf(),
    action: ((TitleTabButtonType) -> Unit)? = null
) {
    val pagePresenter:PageComposePresenter = get()
    val pageAppViewModel:PageAppViewModel = get()
    var onTop by remember { mutableStateOf( true ) }
    val scrollState by remember { mutableStateOf( parentScrollState ?: ScrollState(-1) ) }
    if (scrollState.isScrollInProgress) {
        val pos = scrollState.value
        if(pos == 0 && !onTop) onTop = true
        else if(onTop && pos != 0) onTop = false
    }

    AppTheme {
        Column(
            modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = margin?.dp ?: type.margin.dp)
                    .padding(vertical = type.margin.dp),
                contentAlignment = if (alignment == TextAlign.Start) Alignment.CenterStart else Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (useBack){
                        ImageButton(
                            defaultImage = R.drawable.back
                        ) {
                            action?.let { it(TitleTabButtonType.Back) }
                        }
                    }
                    title?.let {
                        if (alignment == TextAlign.Start) {
                            Text(
                                it,
                                fontSize = type.textSize.sp,
                                fontWeight = type.textFamily,
                                color = ColorApp.black,
                                textAlign = TextAlign.Start,
                                maxLines = lineLimit
                            )
                        } else {
                            Text(
                                it,
                                fontSize = type.textSize.sp,
                                fontWeight = type.textFamily,
                                color = ColorApp.black,
                                textAlign = TextAlign.Center,
                                maxLines = lineLimit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1.0f)
                            )
                        }
                    }
                    sortButton?.let {
                        SortButton(
                            type = SortButtonType.Stroke,
                            sizeType = SortButtonSizeType.Small,
                            icon = R.drawable.search,
                            text = it,
                            color = ColorApp.grey400,
                            isSort = true,
                            petProfile = sortPetProfile
                        ) {
                            sort?.let { it() }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DimenMargin.tiny.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f))
                    buttons.forEachIndexed { index, button ->
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            horizontalArrangement = Arrangement.spacedBy(DimenMargin.micro.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            button.text?.let {
                                Box(modifier = Modifier.wrapContentSize()){
                                    Text(
                                        stringResource(id = it),
                                        fontSize = FontSize.thin.sp,
                                        color = button.color
                                    )
                                    action?.let {
                                        TransparentButton(
                                            modifier = Modifier.matchParentSize()) {
                                            it(button)
                                        }
                                    }

                                }
                            }
                            button.icon?.let {

                                ImageButton(
                                    defaultImage = it,
                                    iconText =  if (icons.count() > index) icons[index] else null,
                                    defaultColor = button.color
                                ) {
                                    action?.let { it(button) }
                                }
                            }

                        }
                    }
                }
            }
            if(type == TitleTabType.Page) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DimenLine.light.dp)
                        .background(if(onTop) ColorTransparent.clear else ColorApp.grey50)
                )
            }
        }
    }
}

@Preview
@Composable
fun TitleTabComposePreview() {
    Koin(appDeclaration = { modules(pageModelModule) }) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(420.dp)
                .background(ColorApp.white),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TitleTab(
                useBack = true,
                title = "Page Title",
                buttons = arrayListOf(TitleTabButtonType.Add, TitleTabButtonType.AddChat),
                icons = arrayListOf("N", null),
                sortButton = "sort"
            ) {

            }
            TitleTab(
                useBack = true,
                title = "Page Title",
                buttons = arrayListOf(TitleTabButtonType.ManageDogs)
            ) {

            }
        }
    }
}