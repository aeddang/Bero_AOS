package com.ironraft.pupping.bero.store.walk
import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ironraft.pupping.bero.AppSceneObserver
import com.ironraft.pupping.bero.store.provider.DataProvider
import com.lib.page.PageLifecycleUser
import com.ironraft.pupping.bero.store.provider.model.User
import com.ironraft.pupping.bero.store.walk.model.Mission
import com.ironraft.pupping.bero.store.walk.model.Place
import com.lib.util.secToMinString
import java.time.LocalDateTime
import kotlin.math.ceil

enum class WalkStatus {
    Ready, Walking
}
class WalkManager(
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
        fun viewDistance(value:Double, unit:String? = "km") : String {
            val v = String.format("%.2f",(value / 1000.0))
            unit?.let {
                return "$v $it"
            }
            return v

        }
        fun viewDuration(value:Double) : String {
            return value.secToMinString()
        }
        fun getPoint(value:Double) : Int {
            return ceil(value/100.0).toInt() //+ 5인증샷 점수
        }
        fun getExp(value:Double) : Double {
            return ceil(value/100.0)
        }
    }
    private val appTag = javaClass.simpleName
    private val user: User = dataProvider.user
    var missionUsers:List<Mission> = listOf()
    var missionUsersSummary:ArrayList<Mission> = arrayListOf()
    var originPlaces:List<Place> = listOf()
    var places:ArrayList<Place> = arrayListOf()
    var placesSummary:ArrayList<Place> = arrayListOf()
    var startTime:LocalDateTime = LocalDateTime.now()
    var startLocation:Location? = null
    var updateLocation:Location? = null
    var updateZipCode:String? = null
    var completedMissions:ArrayList<Int> = arrayListOf()
    var completedWalk:Mission? = null

    val status:MutableLiveData<WalkStatus> = MutableLiveData<WalkStatus>(WalkStatus.Ready)
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {}
    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {}

    fun endWalk(){}
}