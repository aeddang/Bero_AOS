package com.ironraft.pupping.bero.scene.page.walk.pop

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.scene.component.item.PetProfileUser
import com.ironraft.pupping.bero.scene.component.list.FriendListType
import com.ironraft.pupping.bero.scene.page.walk.PageWalkViewModel
import com.ironraft.pupping.bero.store.PageRepository
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.PetData
import com.ironraft.pupping.bero.store.api.rest.UserAndPet
import com.ironraft.pupping.bero.store.provider.model.FriendStatus
import com.ironraft.pupping.bero.store.provider.model.PetProfile
import com.ironraft.pupping.bero.store.walk.WalkManager
import com.lib.page.ListViewModel
import com.lib.util.rememberForeverLazyListState
import com.skeleton.component.item.EmptyData
import com.skeleton.component.item.EmptyItem
import com.skeleton.component.item.EmptyItemType
import com.skeleton.component.tab.MenuTab
import com.skeleton.theme.*
import com.skeleton.view.button.*
import dev.burnoo.cokoin.get


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun PopupWalkUser(
    viewModel: PageWalkViewModel,
    close:() -> Unit
) {
    val owner = LocalLifecycleOwner.current
    val walkManager: WalkManager = get()
    val repository: PageRepository = get()
    var isFriend by remember { mutableStateOf( false )}
    val walkUserListViewModel: WalkUserListViewModel by remember { mutableStateOf(
        WalkUserListViewModel(repo = repository).initSetup(owner)
    )}
    val scrollState: LazyListState = rememberLazyListState()
    val friends by remember { mutableStateOf( walkManager.missionUsers.filter { it.isFriend } )}
    val aroundUser by remember { mutableStateOf( walkManager.missionUsers.filter { !it.isFriend } )}
    val recommands by walkUserListViewModel.listDatas.observeAsState()
    val recommandEmpty by walkUserListViewModel.isEmpty.observeAsState()
    fun onUpdate(friend:Boolean):Boolean{
        walkManager.missionUsers.forEach { it.setDistance( walkManager.currentLocation.value) }
        isFriend = friend
        if(friend)
            walkUserListViewModel.resetLoad()
        else
            walkUserListViewModel.reset()
        return true
    }

    val isInit by remember { mutableStateOf( onUpdate(isFriend) )}

    AppTheme {
        if(isInit) {
            Column(
                Modifier
                    .wrapContentSize().heightIn(0.dp, 810.dp)
                    .padding(DimenMargin.regular.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.End
            ) {
                ImageButton(
                    defaultImage = R.drawable.close
                ) {
                    close()
                }
                MenuTab(
                    modifier = Modifier.padding( top = DimenMargin.tiny.dp ),
                    buttons = listOf(
                        stringResource(id = R.string.sort_aroundMe),
                        stringResource(id = R.string.sort_myFriends),
                    ),
                    selectedIdx = if (isFriend) 1 else 0
                ) {
                    isFriend = it != 0
                    onUpdate(isFriend)
                }
                LazyColumn(
                    modifier = Modifier.weight(1.0f),
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(DimenMargin.regularExtra.dp),
                    contentPadding = PaddingValues(vertical = DimenMargin.medium.dp)
                ) {

                    if (!isFriend) {
                        item {
                            Text(
                                stringResource(id = R.string.aroundUser),
                                fontSize = FontSize.regular.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorApp.black,
                                textAlign = TextAlign.Start
                            )
                        }
                        if(aroundUser.isEmpty()){
                            item {
                                EmptyItem(type = EmptyItemType.MyList)
                            }
                        }
                        items(aroundUser, key = {it.index}) { data->
                            PetProfileUser(
                                profile = data.petProfile ?: PetProfile(),
                                friendStatus = FriendStatus.Norelation,
                                distance = data.distanceFromMe
                            ) {
                                close()
                                /*
                                DispatchQueue.main.asyncAfter(deadline: .now()+0.1) {
                                     self.pagePresenter.openPopup(PageProvider.getPageObject(.popupWalkUser).addParam(key: .data, value: data))
                                }
                                */
                            }
                        }
                    } else {
                        item {
                            Text(
                                stringResource(id = R.string.recommandUser),
                                fontSize = FontSize.regular.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorApp.black,
                                textAlign = TextAlign.Start
                            )
                        }
                        recommands?.let { datas->
                            if(recommandEmpty == true){
                                item {
                                    EmptyData(text = stringResource(id = R.string.needTag))
                                }
                            }
                            items(datas, key = {it.id}) { data->
                                PetProfileUser(
                                    profile = data,
                                    friendStatus = FriendStatus.Norelation
                                ) {
                                    close()
                                    /*
                                    DispatchQueue.main.asyncAfter(deadline: .now()+0.1) {
                                         self.pagePresenter.openPopup(PageProvider.getPageObject(.popupWalkUser).addParam(key: .data, value: data))
                                    }
                                    */
                                }
                            }
                        }
                        item {
                            Text(
                                stringResource(id = R.string.sort_myFriends),
                                fontSize = FontSize.regular.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorApp.black,
                                textAlign = TextAlign.Start
                            )
                        }
                        if(friends.isEmpty()){
                            item {
                                EmptyItem(type = EmptyItemType.MyList)
                            }
                        }
                        items(friends, key = {it.index}) { data->
                            PetProfileUser(
                                profile = data.petProfile ?: PetProfile(),
                                friendStatus = FriendStatus.Chat,
                                distance = data.distanceFromMe
                            ) {
                                close()
                                /*
                                DispatchQueue.main.asyncAfter(deadline: .now()+0.1) {
                                     self.pagePresenter.openPopup(PageProvider.getPageObject(.popupWalkUser).addParam(key: .data, value: data))
                                }
                                */
                            }
                        }
                    }
                }
            }
        }
    }
}
