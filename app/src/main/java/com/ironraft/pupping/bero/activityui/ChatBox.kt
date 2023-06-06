package com.ironraft.pupping.bero.activityui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.SceneEvent
import com.ironraft.pupping.bero.SceneEventType
import com.ironraft.pupping.bero.scene.page.chat.component.ChatRoomListItemData
import com.ironraft.pupping.bero.scene.page.viewmodel.PageID
import com.ironraft.pupping.bero.scene.page.viewmodel.PageParam
import com.ironraft.pupping.bero.scene.page.viewmodel.PageProvider
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.ApiValue
import com.ironraft.pupping.bero.store.api.rest.ChatRoomData
import com.lib.page.ComponentViewModel
import com.skeleton.theme.*
import com.skeleton.view.button.ImageButton
import com.skeleton.view.button.TransparentButton
import dev.burnoo.cokoin.get

enum class ChatStatus {
    Static, Instance, Hidden
}

class ChatFunctionViewModel(val repo: PageRepository) : ComponentViewModel() {
    var sendUserId:String = ""
    var focusManager:FocusManager? = null
    val currentStatus = MutableLiveData<ChatStatus>(ChatStatus.Hidden)
    val inputValue = MutableLiveData<String>("")


    fun initSetup(owner: LifecycleOwner,focusManager:FocusManager? = null): ChatFunctionViewModel {
        setDefaultLifecycleOwner(owner)
        this.focusManager = focusManager
        return this
    }

    private fun close(){
        repo.appSceneObserver.isActiveChat.value = false
        sendUserId = ""
        currentStatus.value = ChatStatus.Hidden
        focusManager?.clearFocus()
    }
    private fun open(userId:String?){
        checkFirstUser()
        userId?.let { sendUserId = it }
        currentStatus.value = ChatStatus.Instance

    }
    private fun setup(userId:String?, isOn:Boolean){
        repo.appSceneObserver.isActiveChat.value = true
        checkFirstUser()
        userId?.let { sendUserId = it }
        currentStatus.value = ChatStatus.Static
    }

    fun send(){
        val input = inputValue.value ?: return
        val q = ApiQ(tag, ApiType.SendChat,  contentID = sendUserId, requestData = input)
        repo.dataProvider.requestData(q)
    }

    private fun checkChatRoom() {
        val q = ApiQ(tag,
            ApiType.GetChatRooms,
            contentID = sendUserId,
            page = 0,
            pageSize = ApiValue.PAGE_SIZE

        )
        repo.dataProvider.requestData(q)
    }

    private fun checkFirstUser() {
        if(!repo.storage.isFirstChat) return
        repo.storage.isFirstChat = false
        repo.appSceneObserver.alert.value = ActivitAlertEvent(
            type = ActivitAlertType.Alert,
            text = repo.pagePresenter.activity.getString(R.string.alert_firstChatMessage)
        )
    }
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.appSceneObserver.event.observe(owner){
            val evt = it ?: return@observe
            val value = evt.value as? String
            val isOn = evt.isOn
            when(evt.type){
                SceneEventType.CloseChat -> close()
                SceneEventType.SendChat -> open(value)
                SceneEventType.SetupChat -> setup(value, isOn)
                else ->{}
            }
        }
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe
            if(res.id != tag) return@observe
            when ( res.type ){
                ApiType.SendChat -> {
                    if(res.contentID != sendUserId) return@observe
                    inputValue.value = ""
                    when ( currentStatus.value ){
                        ChatStatus.Instance ->{
                            checkChatRoom()
                            close()

                        }
                        ChatStatus.Static -> focusManager?.clearFocus()
                        else -> {}
                    }
                }
                ApiType.GetChatRooms -> {
                    val lists = res.data as? List<*> ?: return@observe
                    val datas = lists.filterIsInstance<ChatRoomData>()
                    val checkId = res.contentID
                    datas.find { d-> d.receiver?.userId == checkId }?.let { data->
                        val item = ChatRoomListItemData().setData(data, idx = 0)
                        repo.pagePresenter.openPopup(
                            PageProvider.getPageObject(PageID.ChatRoom)
                                .addParam(PageParam.data, item)
                        )
                    }
                }
                else ->{}
            }
        }

    }
    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.disposeDefaultLifecycleOwner(owner)
        repo.disposeLifecycleOwner(owner)
    }
}

@Composable
fun ChatBox(
    modifier : Modifier,
    chatFunctionViewModel: ChatFunctionViewModel
) {
    val appTag = "ChatBox"
    val appSceneObserver: AppSceneObserver =  get()
    val focusManager = LocalFocusManager.current

    val status by chatFunctionViewModel.currentStatus.observeAsState()
    val input by chatFunctionViewModel.inputValue.observeAsState()
    var isEditing:Boolean by remember { mutableStateOf(false) }

    fun onInit():Boolean{
        chatFunctionViewModel.focusManager = focusManager
        return true
    }
    val isInit:Boolean by remember { mutableStateOf(onInit()) }
    AppTheme {
        if (isInit) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                if(isEditing) {
                    TransparentButton {
                        when ( status ){
                            ChatStatus.Instance ->appSceneObserver.event.value = SceneEvent(SceneEventType.CloseChat)
                            else -> focusManager.clearFocus()
                        }
                    }
                }
                if(status != ChatStatus.Hidden) {
                    Box(
                        modifier = modifier
                            .background(ColorApp.white)
                            .padding(
                                horizontal = DimenApp.pageHorinzontal.dp,
                                vertical = DimenMargin.tiny.dp
                            )
                    ) {
                        val focusRequester = remember { FocusRequester() }
                        if (status == ChatStatus.Instance) {
                            LaunchedEffect(true) {
                                if (!isEditing) focusRequester.requestFocus()
                            }
                        }
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusEvent {
                                    isEditing = it.isFocused
                                },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    chatFunctionViewModel.send()
                                }
                            ),
                            value = input ?: "",
                            onValueChange = {
                                chatFunctionViewModel.inputValue.value = it
                            },
                            shape = RoundedCornerShape(DimenRadius.thin.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor = ColorApp.white,
                                cursorColor = ColorBrand.primary,
                                focusedBorderColor = ColorBrand.primary,
                                unfocusedBorderColor = ColorApp.grey200
                            ),
                            singleLine = false,
                            maxLines = 3,
                            trailingIcon = {
                                ImageButton(
                                    defaultImage = R.drawable.send,
                                    defaultColor = ColorApp.grey200,
                                    isSelected = input?.isNotEmpty() ?: false
                                ) {
                                    if(input?.isEmpty() != false) return@ImageButton
                                    chatFunctionViewModel.send()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

