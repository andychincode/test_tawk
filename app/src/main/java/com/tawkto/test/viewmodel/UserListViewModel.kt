package com.tawkto.test.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tawkto.test.TestApplication
import com.tawkto.test.data.FetchUserListCallback
import com.tawkto.test.data.NoteRepository
import com.tawkto.test.model.Note
import com.tawkto.test.data.UserRepository
import com.tawkto.test.model.User

class UserListViewModel(private val listRepository: UserRepository) : ViewModel() {
    private val repository = NoteRepository(TestApplication.instance)

    private val _users = MutableLiveData<Pair<List<User>, Boolean>>().apply { value = Pair(emptyList(), false) }
    val users: LiveData<Pair<List<User>, Boolean>> = _users

    var liveDataNote: LiveData<List<Note>>? = null

    private val _onError = MutableLiveData<String?>()
    val onError: LiveData<String?> = _onError

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    fun loadUsers(page: Int) {
        listRepository.fetchUserList(page, object : FetchUserListCallback<User> {
            override fun onSuccess(data: List<User>?) {
                if (data.isNullOrEmpty()) {
                    _isEmptyList.postValue(true)
                } else {
                    _users.postValue(_users.value?.first?.let { Pair(it.plus(data), true) })
                }
            }

            override fun onError(error: String?) {
                _onError.postValue(error)
            }
        })
    }

    // clear users
    fun clearUsers() {
        _users.value = Pair(emptyList(), false)
    }

    // get all users's note from Room database
    fun getAllUserNotes() : LiveData<List<Note>>? {
        liveDataNote = repository.getAllNotes()
        return liveDataNote
    }
}