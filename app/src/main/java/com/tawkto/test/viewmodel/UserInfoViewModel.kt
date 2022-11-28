package com.tawkto.test.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tawkto.test.TestApplication
import com.tawkto.test.data.FetchUserCallback
import com.tawkto.test.data.NoteRepository
import com.tawkto.test.model.Note
import com.tawkto.test.data.UserRepository
import com.tawkto.test.model.User
import kotlinx.coroutines.launch

class UserInfoViewModel(private val listRepository: UserRepository) : ViewModel() {
    private val repository = NoteRepository(TestApplication.instance)

    private val _user = MutableLiveData<User>().apply { value = null }
    var user: LiveData<User> = _user

    var liveDataNote: LiveData<Note>? = null

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _onError = MutableLiveData<String?>()
    val onError: LiveData<String?> = _onError

    fun loadUser(login: String) {
        _isLoading.value = true
        listRepository.fetchUserInfo(login, object : FetchUserCallback<User> {
            override fun onSuccess(data: User) {
                _isLoading.postValue(false)
                _user.postValue(data)
            }

            override fun onError(error: String?) {
                _isLoading.postValue(false)
                _onError.postValue(error)
            }
        })
    }

    fun insertData(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun updateData(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun getUserNote(userId: Int) : LiveData<Note>? {
        liveDataNote = repository.getNote(userId)
        return liveDataNote
    }
}