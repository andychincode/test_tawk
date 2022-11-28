package com.tawkto.test.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val login: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("blog")
    val blog: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("followers")
    var followers: Int,
    @SerializedName("following")
    var following: Int
) : Serializable
