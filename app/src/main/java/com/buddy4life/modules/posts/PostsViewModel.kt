package com.buddy4life.modules.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.buddy4life.model.Post.Post

class PostsViewModel : ViewModel() {

    var posts: LiveData<MutableList<Post>>? = null

}
