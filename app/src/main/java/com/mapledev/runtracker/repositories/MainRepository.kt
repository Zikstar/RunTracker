package com.mapledev.runtracker.repositories

import com.mapledev.runtracker.db.Run
import com.mapledev.runtracker.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val runDao: RunDAO
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    //For Statistics Fragment
    fun getTotalTimeOfAllRunsInMillis() = runDao.getTotalTimeOfAllRunsInMillis()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

}