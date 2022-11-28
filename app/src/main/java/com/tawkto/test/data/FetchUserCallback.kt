package com.tawkto.test.data

interface FetchUserCallback<T> {
    fun onSuccess(data: T)
    fun onError(error: String?)
}