package com.tawkto.test.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tawkto.test.data.UserRepository
import com.tawkto.test.viewmodel.UserInfoViewModel
import com.tawkto.test.viewmodel.UserListViewModel

class ViewModelFactory(private val listRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass == UserListViewModel::class.java)
            UserListViewModel(listRepository) as T
        else
            UserInfoViewModel(listRepository) as T
    }
}