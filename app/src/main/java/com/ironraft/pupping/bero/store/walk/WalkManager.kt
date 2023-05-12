package com.ironraft.pupping.bero.store.walk
import androidx.lifecycle.LifecycleOwner
import com.lib.page.PageLifecycleUser
import com.ironraft.pupping.bero.store.provider.model.User
import com.lib.util.secToMinString
import kotlin.math.ceil

class WalkManager(private val user: User) : PageLifecycleUser {
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
    override fun setDefaultLifecycleOwner(owner: LifecycleOwner) {}
    override fun disposeDefaultLifecycleOwner(owner: LifecycleOwner) {}


}