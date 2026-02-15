package com.example.logtrack_1_1.di

import com.example.logtrack_1_1.data.repo.TrackerRepository

interface AppContainer {
    val repository: TrackerRepository
}