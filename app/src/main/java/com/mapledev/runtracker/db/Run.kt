package com.mapledev.runtracker.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

/**@param timeStamp is when the run took place in
 *  milliseconds
 * @param timeInMillis is how long the run was */
@Entity(tableName = "running_table")
data class Run(
    var previewImage: Bitmap? = null,
    var timeStamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0,
    var distanceInMeters: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}