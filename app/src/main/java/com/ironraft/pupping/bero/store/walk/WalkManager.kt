package com.ironraft.pupping.bero.store.walk
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.store.api.ApiField
import com.ironraft.pupping.bero.store.api.ApiQ
import com.ironraft.pupping.bero.store.api.ApiType
import com.ironraft.pupping.bero.store.api.rest.WalkUserData
import com.ironraft.pupping.bero.store.api.rest.WalkadditionalData
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageLifecycleUser
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.ironraft.pupping.bero.store.walk.model.Place
import com.ironraft.pupping.bero.store.walk.model.WalkPath
import com.lib.observer.LocationObserver
import com.lib.page.PagePresenter
import com.lib.util.DataLog
import com.lib.util.PageLog
import com.lib.util.distance
import com.lib.util.replace
import com.lib.util.secToMinString
import com.lib.util.showCustomToast
import java.util.Date
import java.util.HashMap
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.math.ceil

data class WalkMapData(
    var loc:LatLng? = null,
    var zoom:Float? = null
)
enum class WalkEvenType {
    ViewTutorial,
    Start, End, Completed,
    ChangeMapStatus, UpdatedPlaces, UpdatedUsers,
    FindPlace,
    UpdateViewLocation, UpdatedPath
}
data class WalkEvent(
    val type:WalkEvenType,
    var value:Any? = null
)
enum class WalkUiEventType {
    MoveMap, CloseAllPopup
}
data class WalkUiEvent(
    val type:WalkUiEventType,
    var value:WalkMapData? = null
)
enum class WalkError {
    AccessDenied, RetRoute, UpdatedMissions
}
enum class WalkStatus {
    Ready, Walking
}
class WalkManager(
    val ctx: Context,
    private var locationObserver:LocationObserver,
    private var pagePresenter:PagePresenter,
    private var appSceneObserver:AppSceneObserver,
    private val dataProvider: DataProvider) : PageLifecycleUser {
    companion object{
        var todayWalkCount:Int = 0
        const val distanceUnit:Double = 5000.0
        const val nearDistance:Double = 20.0
        const val minDistance:Double = 100.0
        const val limitedUpdateImageSize:Int = 9
        fun viewSpeed(value:Double, unit:String? = "kmPerH") : String {
            val v = String.format("%.1f",(value / 1000.0))
            unit?.let {
                return "$v $it"
            }
            return v
        }
        fun viewDistance(value:Double?, unit:String? = "km") : String {
            val v = String.format("%.2f",((value ?: 0.0) / 1000.0))
            unit?.let {
                return "$v $it"
            }
            return v

        }
        fun viewDuration(value:Double?) : String {
            val v = value ?: 0.0
            return v.secToMinString()
        }
        fun getPoint(value:Double?) : Int {
            val v = value ?: 0.0
            return ceil(v/100.0).toInt() //+ 5인증샷 점수
        }
        fun getExp(value:Double?) : Double {
            val v = value ?: 0.0
            return ceil(v/100.0)
        }
    }
    private val appTag = javaClass.simpleName
    private val user: User = dataProvider.user
    var missionUsers:List<Mission> = listOf(); private set
    var missionUsersSummary:ArrayList<Mission> = arrayListOf(); private set
    var originPlaces:List<Place> = listOf(); private set
    var places:ArrayList<Place> = arrayListOf(); private set
    var placesSummary:ArrayList<Place> = arrayListOf(); private set
    var startTime:Date = Date(); private set
    var startLocation:LatLng? = null; private set
    var updateLocation:LatLng? = null; private set
    var updateZipCode:String? = null; private set
    var completedMissions:ArrayList<Int> = arrayListOf(); private set
    var completedWalk:Mission? = null; private set

    val uiEvent:MutableLiveData<WalkUiEvent?> = MutableLiveData(null)
    val event:MutableLiveData<WalkEvent?> = MutableLiveData(null)
    val error:MutableLiveData<WalkError?> = MutableLiveData(null)
    val status:MutableLiveData<WalkStatus> = MutableLiveData<WalkStatus>(WalkStatus.Ready)
    val walkTime:MutableLiveData<Double> = MutableLiveData(0.0)
    val walkDistance:MutableLiveData<Double> = MutableLiveData(0.0)
    val viewMission:MutableLiveData<Mission?> = MutableLiveData(null)
    val viewPlace:MutableLiveData<Place?> = MutableLiveData(null)
    val currentLocation:MutableLiveData<LatLng?> = MutableLiveData(null)
    val isMapLoading:MutableLiveData<Boolean> = MutableLiveData(false)
    val currentDistanceFromMission:MutableLiveData<Double?> = MutableLiveData(null)
    val playPoint:MutableLiveData<Int> = MutableLiveData(0)
    val playExp:MutableLiveData<Double> = MutableLiveData(0.0)
    val isSimpleView:MutableLiveData<Boolean> = MutableLiveData(false)
    var walkId:Int? = null; private set
    var walkPath:WalkPath? = null; private set
    var updateImages:ArrayList<Bitmap> = arrayListOf(); private set
    var updateImageLocations:ArrayList<LatLng> = arrayListOf(); private set

    val nearDistance:Double = WalkManager.nearDistance
    val farDistance:Double = 2000.0
    val updateTime:Int = 5
    var isBackGround:Boolean = false

    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {
        locationObserver.finalLocation.observe(owner){
            val loc = it ?: return@observe
            updateLocation(loc)
            when (status.value){
                WalkStatus.Ready -> locationObserver.requestMe(false, appTag)
                else -> {}
            }
        }
        locationObserver.requestMe(true, appTag)
    }
    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {
        locationObserver.requestMe(false, appTag)
        locationObserver.finalLocation.removeObservers(owner)
    }
    fun firstWalk(){
        event.value = WalkEvent(WalkEvenType.ViewTutorial, value = R.raw.tutorial_1)
    }
    fun firstWalkStart(){
        if(todayWalkCount < 1){
            event.value = WalkEvent(WalkEvenType.ViewTutorial, value = R.raw.tutorial_2)
        }
    }
    private fun clearAllMapStatus(){
        missionUsers = listOf()
        originPlaces = listOf()
        places = arrayListOf()
        missionUsersSummary = arrayListOf()
        placesSummary = arrayListOf()
        updateLocation = null
        updateZipCode = null
    }

    fun resetMapStatus(location:LatLng? = null){
        clearAllMapStatus()
        event.value = WalkEvent(WalkEvenType.ChangeMapStatus)
        val loc = location ?: currentLocation.value ?: return
        updateMapStatus(loc, isCheckDistence = false)
    }

    fun clearMapUser(){
        missionUsers = arrayListOf()
        missionUsersSummary = arrayListOf()
    }

    fun updateMapStatus(location:LatLng, isCheckDistence:Boolean = true){
        if(isCheckDistence && updateLocation != null){
            updateLocation?.let { prev->
                val distance = prev.distance(location)
                if (distance <= WalkManager.distanceUnit) {
                    DataLog.d("already updated", appTag)
                    return
                }
            }

        }
        updateMapPlace(location)
        updateMapUser(location)
        event.value = WalkEvent(WalkEvenType.UpdateViewLocation, value = location)
    }

    fun replaceMapStatus(location:LatLng){
        clearAllMapStatus()
        updateMapPlace(location)
        updateMapUser(location)
        event.value = WalkEvent(WalkEvenType.UpdateViewLocation, value = location)
    }

    fun updateMapPlace(location:LatLng){
        updateLocation = location
        val params = HashMap<String, String>()
        params[ApiField.lat] = location.latitude.toString()
        params[ApiField.lng] = location.longitude.toString()
        params[ApiField.radius] = WalkManager.distanceUnit.toString()
        params[ApiField.searchType] = ""
        params[ApiField.placeType] = "Manual"
        params[ApiField.zipCode] = ""
        val q = ApiQ(appTag, ApiType.GetPlace, query = params )
        dataProvider.requestData(q)
    }
    fun updateMapUser(location:LatLng){
        if (missionUsers.isEmpty()) {
            val params = HashMap<String, String>()
            params[ApiField.lat] = location.latitude.toString()
            params[ApiField.lng] = location.longitude.toString()
            params[ApiField.radius] = "1000"
            params[ApiField.latestWalkMin] = "60000"
            val q = ApiQ(appTag, ApiType.SearchLatestWalk, query = params )
            dataProvider.requestData(q)
        }
    }

    fun updateSimpleView(view:Boolean) {
        if (status.value != WalkStatus.Walking) {
            isSimpleView.value = false
            return
        }
        isSimpleView.value = view
    }

    fun updateReward(exp:Double, point:Int) {
        if (status.value != WalkStatus.Walking) {
            return
        }
        playPoint.value = playPoint.value?.plus(point)
        playExp.value = playExp.value?.plus(exp)
    }
    fun startMap() {
        if  (status.value != WalkStatus.Walking && currentLocation.value != null) {return}
        requestLocation()
    }
    fun endMap() {}

    fun requestWalk(){
        val loc = currentLocation.value ?: return
        val withProfiles = dataProvider.user.pets.filter{it.isWith}.map { it.petId }

        val param = HashMap<String, Any>()
        param["petIds"] = withProfiles
        val geo = HashMap<String, Any>()
        geo["lat"] = loc.latitude
        geo["lng"] = loc.longitude
        param["location"] = geo
        val q = ApiQ(appTag, ApiType.RegistWalk, body = param )
        dataProvider.requestData(q)
    }

    private fun startWalk(){
        startTime = Date()
        startLocation = currentLocation.value
        event.value = WalkEvent(WalkEvenType.Start)
        status.value = WalkStatus.Walking
        startTimer()
        walkPath = WalkPath()
        requestLocation()
        updatePath()
        /*
        if #available(iOS 16.2, *) , let lsm = self.lockScreenManager as? LockScreenManager {
            lsm.startLockScreen(data: .init(title: String.lockScreen.start))
        }
        */
        val loc = currentLocation.value ?: return
        updateMapStatus(loc, isCheckDistence = true)
    }

    fun completeWalk(){
        val mission = Mission().setData(this, pagePresenter.activity)
        completedWalk = mission
        event.value = WalkEvent(WalkEvenType.Completed, value = mission)
    }

    fun endWalk(){
        //endLockScreen()
        completedWalk = null
        walkPath = null
        walkTime.value = 0.0
        walkDistance.value = 0.0
        playExp.value = 0.0
        playPoint.value = 0
        completedMissions = arrayListOf()
        updateImages = arrayListOf()
        updateImageLocations = arrayListOf()
        endTimer()
        event.value = WalkEvent(WalkEvenType.End)
        status.value = WalkStatus.Ready
        walkId = null
        isSimpleView.value = false
        locationObserver.requestMe(false, appTag)
    }
    fun registPlace(){
        filterPlace()
    }

    fun updateAbleCheck():Boolean{
        currentLocation.value?.let {
            Toast(ctx).showCustomToast(
                ctx.getString(R.string.alert_locationDisable),
                pagePresenter.activity
            )
            return false
        }

        if (updateImages.count() >= WalkManager.limitedUpdateImageSize) {
            Toast(ctx).showCustomToast(
                ctx.getString(R.string.walkImageLimitedUpdate).replace(WalkManager.limitedUpdateImageSize.toString()),
                pagePresenter.activity
            )
            return false
        }
        return true
    }
    fun updateStatus(img:Bitmap? = null){
        val loc = currentLocation.value ?: return
        val id = walkId ?: return
        val data = WalkadditionalData(
            loc = loc,
            status = com.ironraft.pupping.bero.store.api.rest.WalkStatus.Walking,
            img = img,
            walkTime = walkTime.value?.toDouble(),
            walkDistance = walkDistance.value
        )
        val q = ApiQ(appTag, ApiType.UpdateWalk, contentID = id.toString(), requestData = data, isOptional = true)
        dataProvider.requestData(q)
    }
    private fun requestLocation() {
        locationObserver.requestMe(true, appTag)
    }
    private fun updateLocation(loc:LatLng) {
        if (status.value == WalkStatus.Ready) {
            currentLocation.value = loc
            //if (places.isEmpty()) filterPlace()
            return
        }
        currentLocation.value?.let { prev ->
            val diff = loc.distance(prev)
            walkDistance.value?.let {
                walkDistance.value =  it + diff
            }
        }
        currentLocation.value = loc
        updateMapStatus(loc)
        if(isBackGround){
            /*
            if #available(iOS 16.2, *) , let lsm = self.lockScreenManager as? LockScreenManager {
                if let place = self.findPlace(loc) {
                    lsm.alertLockScreen(data: .init(
                        title: "FIND",
                    info: (place.title ?? "") + " find!",
                    walkTime: self.walkTime,
                    walkDistance: self.walkDistance))
                } else {
                    lsm.updateLockScreen(data: .init(title: String.lockScreen.walking, walkTime: self.walkTime, walkDistance: self.walkDistance))
                }
            }
            */
        } else {
            findPlace(loc)
        }
    }

    private var timer:Timer? = null
    private fun startTimer(){
        var n = 0
        this.timer = timer(name = appTag, period=1000, daemon = false ){
            val t = Date().time - startTime.time
            walkTime.postValue(t.toDouble())
            n += 1
            //PageLog.d("time $n", appTag)
            if( n == updateTime ){
                //updateStatus()
            }
        }
    }

    private fun endTimer(){
        timer?.cancel()
        timer = null
    }

    private fun updatePath(){
        val loc = currentLocation.value ?: return
        updateImageLocations.add(loc)
        walkPath?.setLocations(updateImageLocations)
        event.value = WalkEvent(WalkEvenType.UpdatedPath)
    }

    private fun filterUser(datas:List<WalkUserData>){
        if(currentLocation.value == null) {
            DataLog.d("filterUser error notfound me", appTag)
            return
        }
        val loc = currentLocation.value ?: return
        val me = dataProvider.user.snsUser?.snsID
        missionUsers = datas.map{Mission().setData(it)}
            .filter{it.userId != me}
            .sortedWith <Mission> (
                Comparator<Mission> { p1, p2 ->
                    val distance1 = p1.location?.distance(loc) ?: 0.0
                    val distance2 = p2.location?.distance(loc) ?: 0.0
                    if(distance1 == distance2) 0
                    else if(distance1 < distance2) -1 else 1
                }
            )

        val summary:ArrayList<Mission> = arrayListOf()
        var prev:Mission? = null
        var idx:Int = 0
        val title = ctx.getString(R.string.appUser).lowercase()
        for(data in missionUsers) {
            data.setRange(idx, width = 0f)
            idx += 1
            data.location?.let { loc->
                if (prev != null){
                    prev?.location?.let { prevLoc ->
                        if (prevLoc.distance(loc) < farDistance) {
                            prev?.addCount(loc=loc)
                        } else {
                            val new = Mission().copySummry(data, title)
                            summary.add(new)
                            prev = new
                        }
                    }
                } else {
                    val new = Mission().copySummry(data, title)
                    summary.add(new)
                    prev = new
                }
            }
        }
        summary.forEach{it.addCompleted()}
        missionUsersSummary = summary
        event.value = WalkEvent(WalkEvenType.UpdatedUsers)
    }

    private fun findWayPoint(loc:LatLng){
        /*
        guard let waypoints = self.currentRoute?.waypoints else {return}
        guard let find = waypoints.firstIndex(where: {$0.distance(from: loc) < self.nearDistance}) else {return}
        self.event = .findWaypoint(index: find, total:waypoints.count)
        */
    }

    private var finalFind:Place? = null

    private fun findPlace(loc:LatLng):Place?{
        val find = places.filter { !it.isMark }.find {(it.location?.distance(loc) ?: 0.0) < WalkManager.nearDistance}
        if(find == null){
            finalFind = null
            return null
        }
        if (finalFind != null) {
            DataLog.d("findPlace already find", appTag)
            return null
        }
        finalFind = find
        DataLog.d("findPlace find " + (find.title ?: ""), appTag)
        event.value = WalkEvent(WalkEvenType.FindPlace, value = find)
        return find
    }

    private fun filterPlace(){
        if(currentLocation.value == null) {
            DataLog.d("filterUser error notfound me", appTag)
            return
        }
        val loc = currentLocation.value ?: return
        DataLog.d("filterPlace start", appTag)

        val initDatas:ArrayList<Place> = arrayListOf()
        val datas:List<Place> = originPlaces
            .sortedWith <Place> (
                Comparator<Place> { p1, p2 ->
                    val distance1 = p1.location?.distance(loc) ?: 0.0
                    val distance2 = p2.location?.distance(loc) ?: 0.0
                    if(distance1 == distance2) 0
                    else if(distance1 < distance2) -1 else 1
                }
            )

        var count:Int = 0
        val completed = datas.filter{it.isMark}
        val new = datas.filter{!it.isMark}
        var fixed:ArrayList<Place> = arrayListOf()
        val limited = WalkManager.nearDistance*5
        for (data in new) {
            data.location?.let { loc->
                val find = fixed.find {
                    it.location?.let {fixLoc->
                        return@find fixLoc.distance(loc) < limited
                    }
                    false
                }
                if (find == null ) {
                    fixed.add(data)
                    count += 1
                }
            }
            if (count == 10) break
        }
        fixed.addAll(completed)
        var idx:Int = 0
        fixed.forEach{
            it.setRange(idx, width = 0f)
            idx += 1
        }
        DataLog.d("filterPlace end " + fixed.count().toString(), appTag)
        places = fixed

        var summary:ArrayList<Place> = arrayListOf()
        val title = ctx.getString(R.string.place).lowercase()
        var prev:Place? = null
        for (data in fixed) {
            data.location?.let { loc->
                if (prev != null){
                    prev?.location?.let { prevLoc ->
                        if (prevLoc.distance(loc) < farDistance) {
                            prev?.addCount(loc=loc)
                        } else {
                            val new = Place().copySummry(data, title)
                            summary.add(new)
                            prev = new
                        }
                    }
                } else {
                    val new = Place().copySummry(data, title)
                    summary.add(new)
                    prev = new
                }

            }
        }
        summary.forEach{it.addCompleted()}
        placesSummary = summary
        event.value = WalkEvent(WalkEvenType.UpdatedPlaces)
    }

}