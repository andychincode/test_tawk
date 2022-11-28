package com.tawkto.test.data

interface FetchUserListCallback<T> {
    fun onSuccess(data: List<T>?)
    fun onError(error: String?)
}