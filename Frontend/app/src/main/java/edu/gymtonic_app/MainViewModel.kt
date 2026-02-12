package edu.gymtonic_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import edu.gymtonic_app.data.Repository
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.LocalDataSource
import edu.gymtonic_app.data.remote.RemoteDataSource

class MainViewModel(application: Application): AndroidViewModel(application){
    private val remoteDataSource: RemoteDataSource
    private val localDataSource: LocalDataSource
    private val repository: Repository

    init {
        val database = GymTonicDatabase.getInstance(application)
        localDataSource = LocalDataSource(database.)
    }
}