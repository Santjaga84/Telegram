package com.example.clontelegram

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.clontelegram.databinding.ActivityMainBinding
import com.example.clontelegram.ui.fragments.MainFragment
import com.example.clontelegram.ui.fragments.register.EnterPhoneNumberFragment
import com.example.clontelegram.ui.objects.AppDrawer
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.database.AUTH
import com.example.clontelegram.utils.AppStates
import com.example.clontelegram.utils.READ_CONTACTS
import com.example.clontelegram.utils.initContacts
import com.example.clontelegram.database.initFirebase
import com.example.clontelegram.database.initUser
import com.example.clontelegram.utils.replaceFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mAppDriver: AppDrawer
    lateinit var mToolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        /* Функция запускается один раз, при создании активити */
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {

            initContacts()
            initFields()
            initFunc()
        }
    }

    private fun initFunc() {
        /* Функция инициализирует функциональность приложения */
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser != null){
            mAppDriver.create()
            replaceFragment(MainFragment(),false)
        }else{
            replaceFragment(EnterPhoneNumberFragment(),false)
        }
    }

    private fun initFields() {
        /* Функция инициализирует переменные */
        mToolbar = binding.mainToolbar
        mAppDriver = AppDrawer()

    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            initContacts()
        }
    }
}