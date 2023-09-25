package com.example.clontelegram

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.example.clontelegram.acivities.RegisterActivity
import com.example.clontelegram.databinding.ActivityMainBinding
import com.example.clontelegram.models.User
import com.example.clontelegram.ui.fragments.ChatsFragment
import com.example.clontelegram.ui.objects.AppDrawer
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.AUTH
import com.example.clontelegram.utils.AppStates
import com.example.clontelegram.utils.AppValueEventListener
import com.example.clontelegram.utils.CHILD_PHOTO_URL
import com.example.clontelegram.utils.NODE_USERS
import com.example.clontelegram.utils.REF_DATABASE_ROOT
import com.example.clontelegram.utils.CURRENT_UID
import com.example.clontelegram.utils.FOLDER_PROFILE_IMAGE
import com.example.clontelegram.utils.REF_STORAGE_ROOT
import com.example.clontelegram.utils.USER
import com.example.clontelegram.utils.initFirebase
import com.example.clontelegram.utils.initUser
import com.example.clontelegram.utils.replaceActivity
import com.example.clontelegram.utils.replaceFragment
import com.example.clontelegram.utils.showToast
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.theartofdev.edmodo.cropper.CropImage

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDriver: AppDrawer
    private lateinit var mToolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            initFields()
            initFunc()
        }

    }

    private fun initFunc() {
        if (AUTH.currentUser != null){
            setSupportActionBar(mToolbar)
            mAppDriver.create()
            replaceFragment(ChatsFragment(),false)
        }else{
            replaceActivity(RegisterActivity())

        }

    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDriver = AppDrawer(this,mToolbar)

    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

}