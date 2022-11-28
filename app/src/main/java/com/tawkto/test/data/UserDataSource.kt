package com.tawkto.test.data

import com.tawkto.test.model.User

interface UserDataSource {
    fun retrieveUsers(page: Int, callback: FetchUserListCallback<User>)
    fun retrieveUser(login: String, callback: FetchUserCallback<User>)
}