package com.tawkto.test.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.tawkto.test.R
import com.tawkto.test.di.Injection
import com.tawkto.test.model.Note
import com.tawkto.test.model.User
import com.tawkto.test.viewmodel.UserInfoViewModel
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: UserInfoViewModel   // viewmodel for userinfo activity
    private lateinit var context: Context

    private lateinit var username: String   // username for current user
    private var userId: Int = -1        // user id for current user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        context = this

        // extract username
        username = ""
        intent.getStringExtra("username").also {
            if (it != null) {
                username = it
            }
        }
        userId = intent.getIntExtra("userid", -1)

        setupViewModel()
        setupUI()
    }

    // view
    private fun setupUI() {
        viewModel.isLoading.observe(this, isLoadingObserver)

        // title
        supportActionBar?.title = username
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // load notes
        viewModel.getUserNote(userId)!!.observe(this, Observer {

            if (it != null) {
                txtNotes.setText(it.note)
            }
        })

        // save note button
        btnSave.setOnClickListener {
            this.saveNote()
        }
    }

    // view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory()) [UserInfoViewModel::class.java]

        viewModel.user.observe(this, renderUser)
        viewModel.isLoading.observe(this, isLoadingObserver)
        viewModel.onError.observe(this, onErrorObserver)
    }

    //observers
    private val renderUser = Observer<User> {
        if (it != null) {
            // set user avatar image
            Glide.with(this).load(it.avatarUrl).into(image_user)
            // set user name
            txtName.text = it.name
            // set user company
            txtCompany.text = it.company
            // set user blog
            txtBlog.text = it.blog

            // followers count
            txtFollowers.text = it.followers.toString()
            // following count
            txtFollowing.text = it.following.toString()
        }
    }

    private val isLoadingObserver = Observer<Boolean> {
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val onErrorObserver = Observer<String?> {
        Snackbar.make(
            contentView,
            getString(R.string.error_fetch_user),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        // load current user details
        viewModel.loadUser(username)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun saveNote() {
        // save notes
        if (userId == -1) return

        val strNote = txtNotes.text.toString().trim()

        if (strNote.isEmpty()) {
            txtNotes.error = context.getString(R.string.error_input_note)
        } else {
            val note = Note(username, strNote, userId)

            viewModel.getUserNote(userId)!!.observe(this) {
                if (it != null) {
                    viewModel.updateData(note)
                } else {
                    viewModel.insertData(note)
                }
            }

            Snackbar.make(contentView, context.getText(R.string.success_input_note), Snackbar.LENGTH_SHORT).show()
        }

    }
}