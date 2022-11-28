package com.tawkto.test.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tawkto.test.R
import com.tawkto.test.model.Note
import com.tawkto.test.model.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_item.view.*

class UserListAdapter(private var users: List<User>, private var userNotes: List<Note>?) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.user_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        vh.bind(users[position], position, userNotes)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(data: List<User>) {
        users = data
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotes(data: List<Note>) {
        userNotes = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val rootView: View = view
        private val avatarView: CircleImageView = view.image_user       // user avatar
        private val tvName: AppCompatTextView = view.txt_user           // user name
        private val tvDescription: AppCompatTextView = view.txt_user_description    // details
        private val noteMarkView: ImageView = view.imgNoteMark      // note mark image

        private val noUser: View = view.no_user
        private val noDetails: View = view.no_details

        fun bind(user: User, position: Int, notes: List<Note>?) {
            // every fourth list item's avatar should have its colours inverted invert image color fourth avatar
            if ((position + 1) % 4 == 0) {
                val negative = floatArrayOf(
                    -1.0f, 0f, 0f, 0f, 255f,
                    0f, -1.0f, 0f, 0f, 255f,
                    0f, 0f, -1.0f, 0f, 255f,
                    0f, 0f, 0f, 1.0f, 0f
                )
                avatarView.colorFilter = ColorMatrixColorFilter(negative)
            } else {
                avatarView.setColorFilter(Color.TRANSPARENT)
            }
            // set avatar image
            Glide.with(rootView).load(user.avatarUrl).into(avatarView)

            // set user login string
            if (user.login?.isNotEmpty() == true) {
                avatarView.setBackgroundColor(rootView.context.getColor(android.R.color.transparent))
                tvName.visibility = View.VISIBLE
                tvName.text = user.login
                tvDescription.visibility = View.VISIBLE

                noUser.visibility = View.GONE
                noDetails.visibility = View.GONE
            }

            // check this user has note
            val matches = notes?.filter {
                it.login == user.login && it.note.isNotEmpty()
            }

            // list item view should have a note icon if there is note information saved for the given user
            if (matches != null && matches.isNotEmpty()) {
                noteMarkView.visibility = View.VISIBLE
                tvDescription.text = matches[0].note
            } else {
                noteMarkView.visibility = View.GONE
            }
        }
    }
}