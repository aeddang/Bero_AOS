package com.ironraft.pupping.bero.store.api

import androidx.compose.foundation.pager.PageSize
import androidx.lifecycle.LifecycleOwner
import com.google.gson.annotations.SerializedName
import com.skeleton.module.network.ErrorType
import okhttp3.Interceptor
import java.io.IOException
import java.util.ArrayList
import java.util.UUID
import kotlin.jvm.Throws

data class ApiResponse<T> (
    @SerializedName("contents") val contents: T? = null,
    @SerializedName("items") val items: List<T>? = null,
    @SerializedName("kind") val kind: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("metadata") val metadata: MetaData? = null
)

data class MetaData (
    @SerializedName("exp") val exp: Double? = null,
    @SerializedName("point") val point: Int? = null,
    @SerializedName("level") val level: Int? = null,
    @SerializedName("nextLevelExp") val nextLevelExp: Double? = null,
    @SerializedName("prevLevelExp") val prevLevelExp: Double?  = null
)

class ApiInterceptor : Interceptor {
    var accesstoken: String = ""
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original = chain.request()
        val request = original.newBuilder()
        if (accesstoken.isNotEmpty()) {
            request.header("Authorization", "Bearer $accesstoken")
        } else {
            request.header("Authorization", "")
        }
        return chain.proceed(request.build())
    }
}

data class ApiQ(val id:String,  val type: ApiType,
    var query:HashMap<String,String>? = null,
    var body:HashMap<String, Any>? = null,
    var contentID:String = "",
    var isOptional:Boolean = false,
    var isLock:Boolean = false,
    var requestData:Any? = null,
    var page:Int = 0,
    var pageSize:Int = ApiValue.PAGE_SIZE,
    var useCoreData:Boolean = true
)

data class ApiSuccess<T>(
    val type:T, var data:Any?,
    val id: String? = null,
    val isOptional:Boolean = false,
    val contentID:String = "",
    val requestData:Any? = null,
    val useCoreData:Boolean = true
){
    val hashId:Int = UUID.randomUUID().hashCode()
}
data class ApiError<T>(
    val type:T , val errorType:ErrorType ,
    val code:String?, val msg:String? = null,
    val id: String? = null,  val isOptional:Boolean = false,
    val requestData:Any? = null
){
    val hashId:Int = UUID.randomUUID().hashCode()
}
data class ApiGroup<T>(
    val type:T, var group: ArrayList<ApiSuccess<T>>,
    var complete:Int,
    var params: ArrayList<Map<String, Any?>?>? = null,
    val isSerial:Boolean = false,
    val owner: LifecycleOwner? = null)
{
    var process:Int = 0 ;private set
    fun finish():Boolean{
        complete --
        process ++
        return complete <= 0
    }
}



