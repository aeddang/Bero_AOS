package com.ironraft.pupping.bero.scene.page.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.SceneEvent
import com.ironraft.pupping.bero.SceneEventType
import com.ironraft.pupping.bero.activityui.ActivitRadioEvent
import com.ironraft.pupping.bero.activityui.ActivitRadioType
import com.ironraft.pupping.bero.koin.pageModelModule
import com.ironraft.pupping.bero.scene.component.tab.TitleTab
import com.ironraft.pupping.bero.scene.component.tab.TitleTabButtonType
import com.ironraft.pupping.bero.scene.component.viewmodel.ReportFunctionViewModel
import com.ironraft.pupping.bero.scene.page.chat.component.ChatList
import com.ironraft.pupping.bero.scene.page.chat.component.ChatRoomListItemData
import com.ironraft.pupping.bero.scene.page.chat.component.ChatUser
import com.ironraft.pupping.bero.scene.page.chat.viewmodel.ChatRoomViewModel

import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.ReportType
import com.lib.page.PageComposePresenter
import com.lib.util.rememberForeverLazyListState
import com.skeleton.component.dialog.RadioBtnData
import com.skeleton.theme.*
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import kotlinx.coroutines.launch

@Composable
fun PageChatRoom(
    modifier: Modifier = Modifier
){
    val appTag = PageID.ChatRoom.value
    val owner = LocalLifecycleOwner.current
    val repository: PageRepository = get()
    val appSceneObserver:AppSceneObserver = get()
    val pagePresenter:PageComposePresenter = get()
    val viewModel: PageViewModel by remember { mutableStateOf(PageViewModel(PageID.ChatRoom, repository).initSetup(owner)) }
    val chatRoomViewModel: ChatRoomViewModel by remember { mutableStateOf(
        ChatRoomViewModel(repo = repository).initSetup(owner, ApiValue.PAGE_SIZE)
    )}
    val reportFunctionViewModel: ReportFunctionViewModel by remember { mutableStateOf(
        ReportFunctionViewModel(repository).initSetup(owner)

    )}
    val scrollState: LazyListState = rememberForeverLazyListState(key = appTag)
    val currentPage = viewModel.currentPage.observeAsState()
    val onClose = viewModel.onClose.observeAsState()

    var roomData:ChatRoomListItemData? by remember { mutableStateOf( null ) }
    val user by chatRoomViewModel.user.observeAsState()
    val pet by chatRoomViewModel.pet.observeAsState()

    currentPage.value?.let { page->
        if(!viewModel.isInit){
            viewModel.isInit = true
            roomData = page.getParamValue(PageParam.data) as? ChatRoomListItemData
            val userId = roomData?.userId ?: ""
            chatRoomViewModel.scrollState = scrollState
            chatRoomViewModel.currentRoomId = roomData?.roomId.toString()
            chatRoomViewModel.currentUserId = userId
            chatRoomViewModel.load()
            appSceneObserver.event.value = SceneEvent(SceneEventType.SetupChat, value = userId)
        }
    }
    onClose.value?.let {
        viewModel.onClose.value = null
        appSceneObserver.event.value = SceneEvent(SceneEventType.CloseChat)
    }

    fun onMore(){
        val currentUser = user ?: return
        reportFunctionViewModel.lazySetup(currentUser.userId, currentUser.representativeName)

        repository.appSceneObserver.radio.value = ActivitRadioEvent(
            type = ActivitRadioType.Select,
            title = repository.pagePresenter.activity.getString(R.string.alert_supportAction),
            radioButtons = arrayListOf(
                RadioBtnData(
                    icon = R.drawable.delete,
                    title = repository.pagePresenter.activity.getString(R.string.button_deleteRoom),
                    index = 0
                ),
                RadioBtnData(
                    icon = R.drawable.block,
                    title = repository.pagePresenter.activity.getString(R.string.button_block),
                    index = 1
                ),
                RadioBtnData(
                    icon = R.drawable.warning,
                    title = repository.pagePresenter.activity.getString(R.string.button_accuse),
                    index = 2
                )
            )
        ){ select ->
            when(select){
                0 -> chatRoomViewModel.exit()
                1 -> reportFunctionViewModel.block()
                2 -> reportFunctionViewModel.accuseUser(ReportType.Chat)
                else -> {}
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    fun onResetScroll(){
        coroutineScope.launch {
            scrollState.animateScrollToItem(chatRoomViewModel.chats.value?.count() ?: 0 )
        }
    }
    val chetEvent = chatRoomViewModel.event.observeAsState()
    chetEvent.value?.let {
        chatRoomViewModel.event.value = null
        onResetScroll()
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(ColorBrand.bg)
            .padding(bottom = DimenApp.bottom.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TitleTab(
            title = roomData?.title,
            useBack = true,
            buttons = arrayListOf(TitleTabButtonType.More)
        ){
            when(it){
                TitleTabButtonType.Back -> {
                    pagePresenter.goBack()
                }
                TitleTabButtonType.More -> onMore()
                else -> {}
            }
        }
        ChatUser(user = user, pet = pet, roomData =  roomData)
        ChatList(
            modifier = Modifier.weight(1.0f),
            scrollState = scrollState, chatRoomViewModel = chatRoomViewModel,
            user = user, pet = pet)

    }
}
@Preview
@Composable
fun PageChatRoomPreview(){
    Koin(appDeclaration = { modules(pageModelModule) }) {
        PageChatRoom(
        )
    }
}
