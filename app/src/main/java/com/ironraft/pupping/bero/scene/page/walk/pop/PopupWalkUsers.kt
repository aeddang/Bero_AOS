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

class WalkUserListViewModel(val repo: PageRepository, id:String = "", initType: FriendListType = FriendListType.Friend)
    : ListViewModel<PetProfile, List<UserAndPet>>() {

    fun initSetup(owner: LifecycleOwner): WalkUserListViewModel {
        setDefaultLifecycleOwner(owner)
        return this
    }

    fun resetLoad(){
        currentPage = 0
        reset()
        load()
    }
    override fun onLoad(page: Int) {
        val q = ApiQ(tag,
            ApiType.GetRecommandationFriends,
            isOptional = true
        )
        repo.dataProvider.requestData(q)
    }

    override fun onLoaded(prevDatas: List<PetProfile>?, addDatas: List<UserAndPet>?): List<PetProfile> {
        addDatas ?: return emptyList()
        val datas = addDatas.map {
            PetProfile().init(
                it.pet ?: PetData(),
                userId = it.user?.userId,
                isFriend = it.user?.isFriend ?: false
            )
        }
        isEmpty.value = datas.isEmpty()
        return datas
    }

    @Suppress("UNCHECKED_CAST")
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        super.setDefaultLifecycleOwner(owner)
        repo.dataProvider.result.observe(owner) {
            val res = it ?: return@observe

            when ( res.type ){
                ApiType.GetRecommandationFriends -> {
                    if(res.page == 0) { reset() }
                    loaded(res.data as? List<UserAndPet> ?: listOf())
                }
                else ->{}
            }
        }
        repo.dataProvider.error.observe(owner) {
            val err = it ?: return@observe
            when ( err.type ){
                ApiType.GetRecommandationFriends -> {
                    isBusy = false
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun PopupWalkUsers(
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
