package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentChangeBioBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.database.CHILD_BIO
import com.example.clontelegram.database.NODE_USERS
import com.example.clontelegram.database.REF_DATABASE_ROOT
import com.example.clontelegram.database.CURRENT_UID
import com.example.clontelegram.database.USER
import com.example.clontelegram.database.setBioToDatabase
import com.example.clontelegram.utils.showToast


/* Фрагмент для изменения информации о пользователе */
class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    private lateinit var binding: FragmentChangeBioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.mAppDriver.disableDrawer()
        binding = FragmentChangeBioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() = with(binding) {
        super.onResume()
        settingsInputBio.setText(USER.bio)
    }

    override fun change(): Unit = with(binding){
        super.change()
        val newBio = settingsInputBio.text.toString()
        setBioToDatabase(newBio)

    }


}