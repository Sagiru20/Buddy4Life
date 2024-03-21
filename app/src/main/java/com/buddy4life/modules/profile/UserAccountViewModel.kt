package com.buddy4life.modules.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.buddy4life.model.User.User

class UserAccountViewModel: ViewModel()  {

    var user: LiveData<User>? = null

}