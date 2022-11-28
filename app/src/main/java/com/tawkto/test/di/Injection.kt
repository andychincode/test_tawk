package com.tawkto.test.di

import androidx.lifecycle.ViewModelProvider
import com.tawkto.test.data.UserDataSource
import com.tawkto.test.data.UserRemoteDataSource
import com.tawkto.test.data.UserRepository
import com.tawkto.test.network.ApiClient

object Injection {
    private val userDataSource: UserDataSource = UserRemoteDataSource(ApiClient)
    private val userRepository = UserRepository(userDataSource)
    private val userViewModelFactory = ViewModelFactory(userRepository)

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return userViewModelFactory
    }
}