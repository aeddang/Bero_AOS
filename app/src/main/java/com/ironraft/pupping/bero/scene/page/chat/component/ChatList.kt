package com.ironraft.pupping.bero.scene.page.chat.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.page.chat.viewmodel.ChatRoomViewModel
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.util.AppUtil
import com.lib.util.isScrolledToEnd
import com.lib.util.toDateFormatter
import com.skeleton.component.item.profile.HorizontalProfile
import com.skeleton.component.item.profile.HorizontalProfileSizeType
import com.skeleton.component.item.profile.HorizontalProfileType
import com.skeleton.theme.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    chatRoomViewModel:ChatRoomViewModel,
    scrollState: LazyListState = rememberLazyListState(),
    pet:PetProfile? = null,
    user: User? = null
) {

    val viewModel: ChatRoomViewModel by remember { mutableStateOf(chatRoomViewModel)}
    val isEmpty by viewModel.isEmpty.observeAsState()
    val isLoading = viewModel.isLoading.observeAsState()
    val chats by viewModel.chats.observeAsState()
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        viewModel.resetLoad()
    }
    val refreshState = rememberPullRefreshState(refreshing, ::refresh)
    isLoading.value?.let{
        if (refreshing && !it) refreshing = false
    }
    val endOfListReached by remember {
        derivedStateOf { scrollState.isScrolledToEnd() }
    }
    if(endOfListReached) {
        //viewModel.continueLoad()
    }

    AppTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(refreshState),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isEmpty == true) {
                Text(
                    AppUtil.networkDate().toDateFormatter("EEEE, MMMM d, yyyy") ?: "",
                    fontSize = FontSize.thin.sp,
                    color = ColorApp.gray400,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = DimenMargin.regular.dp)
                )
                Text(
                    stringResource(id = R.string.chatRoomText),
                    fontSize = FontSize.thin.sp,
                    color = ColorApp.gray300,
                    textAlign = TextAlign.Center
                )
            }
            else if(chats != null) {
                chats?.let { datas ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = DimenApp.pageHorinzontal.dp),
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(DimenMargin.regularUltra.dp),
                        contentPadding = PaddingValues(
                            vertical = DimenMargin.medium.dp
                        )
                    ) {
                        if(datas.isNotEmpty()){
                            datas.firstOrNull()?.originDate?.let {
                                item {
                                    Text(
                                        it.toDateFormatter("EEEE, MMMM d, yyyy") ?: "",
                                        fontSize = FontSize.thin.sp,
                                        color = ColorApp.gray400,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxSize().padding(bottom = DimenMargin.regular.dp)
                                    )
                                }
                            }
                        }
                        items(datas, key = { it.id }) { data ->
                            Column (
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                data.date?.let {
                                    Text(
                                        it.toDateFormatter("EEEE, MMMM d, yyyy") ?: "",
                                        fontSize = FontSize.thin.sp,
                                        color = ColorApp.gray400,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxSize().padding(bottom = DimenMargin.regular.dp)
                                    )
                                }
                                if (!data.isMe) {
                                    if (pet != null) {
                                        HorizontalProfile(
                                            type = HorizontalProfileType.Pet,
                                            sizeType = HorizontalProfileSizeType.Tiny,
                                            imagePath = pet.imagePath.value,
                                            name = pet.name.value,
                                            useBg = false
                                        )
                                    } else {
                                        user?.currentProfile?.let {
                                            HorizontalProfile(
                                                type = HorizontalProfileType.User,
                                                sizeType = HorizontalProfileSizeType.Tiny,
                                                imagePath = it.imagePath.value,
                                                name = it.nickName.value,
                                                useBg = false
                                            )
                                        }
                                    }
                                }
                                data.datas.reversed().forEach { chat ->
                                    ChatItem(
                                        modifier = Modifier.padding(
                                            start = DimenProfile.thin.dp,
                                            bottom = DimenMargin.tiny.dp
                                        ),
                                        data = chat,
                                        onDelete = { viewModel.delete(chat.chatId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else Spacer(modifier = Modifier.fillMaxSize())
            PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

