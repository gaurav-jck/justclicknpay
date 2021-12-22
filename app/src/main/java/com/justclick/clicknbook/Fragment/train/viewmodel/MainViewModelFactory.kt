package com.justclick.clicknbook.Fragment.train.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrainSearchViewModel::class.java)){
            return TrainSearchViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}