package com.example.logtrack_1_1.di

import android.content.Context
import androidx.room.Room
import com.example.logtrack_1_1.data.db.LogTrackDatabase
import com.example.logtrack_1_1.data.repo.TrackerRepository
import com.example.logtrack_1_1.data.repo.TrackerRepositoryImpl

class DefaultAppContainer(context: Context) : AppContainer {

    private val db: LogTrackDatabase =
        Room.databaseBuilder(context, LogTrackDatabase::class.java, "logtrack.db")
            .fallbackToDestructiveMigration()
            .build()

    override val repository: TrackerRepository =
        TrackerRepositoryImpl(
            categoryDao = db.categoryDao(),
            metricDao = db.metricDao(),
            eventDao = db.eventDao(),
            todayDao = db.todayDao()
        )
}