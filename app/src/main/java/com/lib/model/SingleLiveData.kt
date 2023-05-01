package com.lib.model

import android.media.DrmInitData
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleLiveData<T>(initData:T) : MutableLiveData<T>(initData) {
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { t ->
            if (t != null) {
                observer.onChanged(t)
                postValue(null)
            }
        })
    }
}