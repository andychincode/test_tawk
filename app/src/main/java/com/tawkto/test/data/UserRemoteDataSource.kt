package com.tawkto.test.data

import com.tawkto.test.model.User
import com.tawkto.test.network.ApiClient
import io.reactivex.rxjava3.core.Observable

class UserRemoteDataSource(apiClient: ApiClient): UserDataSource {
    private lateinit var fetchUsersCall: Observable<List<User>>
    private lateinit var fetchUserCall: Observable<User>

    private val service = apiClient.build()

    override fun retrieveUsers(page: Int, callback: FetchUserListCallback<User>) {
        fetchUsersCall = service.getUserList(page)
        fetchUsersCall.subscribe(
            { value ->  callback.onSuccess(value) },
            { error -> callback.onError(error.message) }
        )
    }

    override fun retrieveUser(login: String, callback: FetchUserCallback<User>) {
        fetchUserCall = service.getUserInfo(login)
        fetchUserCall.subscribe(
                { response -> callback.onSuccess(response) },
                { error -> callback.onError(error.message) }
            )
    }
}