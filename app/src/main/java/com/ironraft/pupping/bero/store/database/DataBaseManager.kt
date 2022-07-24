package com.ironraft.pupping.bero.store.database

import android.content.Context
import com.ironraft.pupping.bero.store.preference.StoragePreference


class DataBaseManager (ctx:Context, val settingPreference: StoragePreference){
    val contentDatabase = ContentDatabase(ctx)
}