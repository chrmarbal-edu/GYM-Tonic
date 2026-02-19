package edu.gymtonic_app.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.Repository
import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.remote.model.SessionManager
import edu.gymtonic_app.data.remote.model.sessionDataStore
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application){
    val remoteDataSource: RemoteDataSource

    val repository: Repository

    val sessionManager: SessionManager

    init{
        remoteDataSource = RemoteDataSource()
        repository = Repository(remoteDataSource)

        val dataStore: DataStore<Preferences> = application.sessionDataStore
        sessionManager = SessionManager(dataStore)
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
            sessionManager.clearSession()
        }
    }
}