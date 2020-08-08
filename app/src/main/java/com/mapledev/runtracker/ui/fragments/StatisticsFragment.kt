package com.mapledev.runtracker.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mapledev.runtracker.R
import com.mapledev.runtracker.ui.viewmodels.MainViewModel
import com.mapledev.runtracker.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel : StatisticsViewModel by viewModels()
}
