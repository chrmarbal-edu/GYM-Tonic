package edu.gymtonic_app.ui.screens.login

import com.facebook.login.LoginManager

class FacebookAuthHelper {
    fun logout() {
        LoginManager.getInstance().logOut()
    }
}
