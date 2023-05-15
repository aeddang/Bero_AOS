package com.ironraft.pupping.bero.store.database

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type


class ApiCoreDataManager (ctx:Context){
    val contentDatabase = ApiDatabase(ctx)
    val gson = Gson()
    suspend fun setData(key:String, data:Any?){
        val insert = data ?: return
        withContext(Dispatchers.IO) {
            val row = contentDatabase.getData(key)
            val jsonString = gson.toJson(data)
            row?.let {
                row.jsonString = jsonString
                contentDatabase.update(row)
                return@withContext
            }
            val insertRow = ApiDatabase.Row(itemId = key, jsonString = jsonString)
            contentDatabase.insert(insertRow)
        }
    }

    inline fun <reified T> getData(key: String): T? {
        val row = contentDatabase.getData(key)
        row ?: return null
        val jsonString = row.jsonString
        return gson.fromJson(jsonString, T::class.java)
    }
    inline fun <reified T> getDatas(key: String, typeToken: Type): T? {
        val row = contentDatabase.getData(key)
        row ?: return null
        val jsonString = row.jsonString
        val gson = GsonBuilder().create()
        return gson.fromJson<T>(jsonString, typeToken)
    }

}