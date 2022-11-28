package com.tawkto.test.data

import com.tawkto.test.model.User

class UserRepository(private val userDataSource: UserDataSource) {
    fun fetchUserList(page: Int, callback: FetchUserListCallback<User>) {
        userDataSource.retrieveUsers(page, callback)
    }

    fun fetchUserInfo(login: String, callback: FetchUserCallback<User>) {
        userDataSource.retrieveUser(login, callback)
    }
}