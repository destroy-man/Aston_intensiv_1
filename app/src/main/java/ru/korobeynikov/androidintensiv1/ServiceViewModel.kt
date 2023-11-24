package ru.korobeynikov.androidintensiv1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServiceViewModel : ViewModel() {

    private var liveData: MutableLiveData<Array<Any>>? = null

    fun getData(): LiveData<Array<Any>>? {
        if (liveData == null)
            liveData = MutableLiveData<Array<Any>>()
        return liveData
    }

    fun loadData(data: Array<Any>) {
        liveData?.value = data
    }
}