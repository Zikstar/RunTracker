package com.mapledev.runtracker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mapledev.runtracker.repositories.MainRepository

class StatisticsViewModel @ViewModelInject constructor(
    mainRepository: MainRepository
) : ViewModel(){
}