package com.tawkto.test.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tawkto.test.R
import com.tawkto.test.di.Injection
import com.tawkto.test.model.User
import com.tawkto.test.view.adapter.ClickListener
import com.tawkto.test.view.adapter.PaginationScrollListener
import com.tawkto.test.view.adapter.RecyclerTouchListener
import com.tawkto.test.view.adapter.UserListAdapter
import com.tawkto.test.viewmodel.UserListViewModel
import kotlinx.android.synthetic.main.activity_user_list.*
import java.util.*
import kotlin.collections.ArrayList

class UserListActivity : AppCompatActivity() {
    private val STATE_IS_LAST_PAGE = "is_last_page"
    private val STATE_CURRENT_PAGE = "current_page"
    private val STATE_IS_LOADING = "is_loading"
    private val STATE_SEARCH_USERS = "search_users"

    private lateinit var listViewModel: UserListViewModel   // viewmodel for userlist activity
    private lateinit var adapter: UserListAdapter   // recycler adapter for user list
    private var searchUsers = mutableListOf<User>()     // user list

    var isLastPage: Boolean = false     // flag for last page
    var isLoading: Boolean = false      // flag for detecting loading or not
    private var currentPage: Int = 0    // current page number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        // checking network connection
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {    // network connected
                    runOnUiThread {
                        onResume()
                    }
                }

                override fun onLost(network: Network) {     // network connection fail
                    Snackbar.make(
                        contentView,
                        getString(R.string.network_unavailable),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        )

        searchUsers = mutableListOf()

        setupViewModel()

        // orientation support
        if (savedInstanceState != null) {
            isLastPage = savedInstanceState.getBoolean(STATE_IS_LAST_PAGE)
            isLoading = savedInstanceState.getBoolean(STATE_IS_LOADING)
            currentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE)

            val array = savedInstanceState.getSerializable(STATE_SEARCH_USERS) as Array<User>
            searchUsers.addAll(array)

            setupUI()
        } else {
            setupUI()

            // load users
            currentPage = 0
            listViewModel.clearUsers()
            listViewModel.loadUsers(0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(STATE_IS_LAST_PAGE, this.isLastPage)
        outState.putBoolean(STATE_IS_LOADING, this.isLoading)
        outState.putInt(STATE_CURRENT_PAGE, this.currentPage)

        val array = searchUsers.toTypedArray()
        outState.putSerializable(STATE_SEARCH_USERS, array)
    }

    private fun setupUI() {
        // users list has to be searchable
        searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchUserWithName(query)
                }
                return true
            }
        })

        // clear button for search bar
        val clearButton: ImageView = searchBar.findViewById(androidx.appcompat.R.id.search_close_btn)
        clearButton.setOnClickListener {
            searchBar.setQuery("", false)
            searchUserWithName("")
            searchBar.onActionViewCollapsed()
        }

        adapter = UserListAdapter(searchUsers, emptyList())

        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter
        adapter.update(getDefaultUsers())

        rvUsers.addOnScrollListener(object : PaginationScrollListener(rvUsers.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                //you have to call loadmore items to get more data
                loadMoreUsers()
            }
        })
        // item click listener for recyclerview
        rvUsers.addOnItemTouchListener(
            RecyclerTouchListener(this, object : ClickListener {
                override fun onClick(view: View?, position: Int) {
                    if (searchUsers.isNotEmpty() && position < searchUsers.size)
                        onUserDetails(searchUsers[position])
                }
            })
        )
    }

    private fun setupViewModel() {
        listViewModel = ViewModelProvider(this, Injection.provideViewModelFactory()) [UserListViewModel::class.java]

        listViewModel.users.observe(this, renderUsers)
        listViewModel.onError.observe(this, onErrorObserver)
        listViewModel.isEmptyList.observe(this, emptyListObserver)
    }

    private fun updateUserList(users: List<User>) {
        adapter.update(users)
        // load notes
        listViewModel.getAllUserNotes()!!.observe(this) { notes -> adapter.updateNotes(notes) }
    }

    //observers
    // observer for user list
    private val renderUsers = Observer<Pair<List<User>, Boolean>> {
        progressBar.visibility = View.GONE

        if (!it.second) return@Observer
        updateUserList(it.first)
        searchUsers.clear()
        searchUsers.addAll(it.first)
        rvUsers.isEnabled = it.second
    }
    // observer for error message
    private val onErrorObserver = Observer<String?> {
        Snackbar.make(
            contentView,
            getString(R.string.error_fetch_users),
            Snackbar.LENGTH_SHORT
        ).show()
    }
    // observer for empty user list (no users)
    private val emptyListObserver = Observer<Boolean> {
        Snackbar.make(
            contentView,
            getString(R.string.no_users),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    // scroll load more
    fun loadMoreUsers() {
        if (listViewModel.users.value?.first.isNullOrEmpty())
            return

        // display spinner while loading data as the last list item
        progressBar.visibility = View.VISIBLE

        val user = listViewModel.users.value?.first?.last() as User
        currentPage = user.id
        isLoading = false
        listViewModel.loadUsers(currentPage)
    }

    fun onUserDetails(user: User) {
        // show user details activity
        val intent = Intent(this, UserInfoActivity::class.java)
        intent.putExtra("username", user.login)
        intent.putExtra("userid", user.id)
        startActivity(intent)
    }

    // set default user list for loading shimmer
    private fun getDefaultUsers() : List<User> {
        val defaultUsers = ArrayList<User>()
        for (i in 0..10) {
            defaultUsers.add(User(i, "", "", "", "", "", "", "", "", "", 0, 0))
        }

        return defaultUsers
    }

    // search users with login string
    private fun searchUserWithName(name: String) {
        searchUsers.clear()

        for (user in listViewModel.users.value!!.first) {
            if (user.login?.lowercase(Locale.getDefault())?.contains(name.lowercase(Locale.getDefault())) == true)
                searchUsers.add(user)
        }

        isLoading = true
        updateUserList(searchUsers)
    }
}