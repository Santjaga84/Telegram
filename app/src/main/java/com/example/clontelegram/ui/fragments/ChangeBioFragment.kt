package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentChangeBioBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.CHILD_BIO
import com.example.clontelegram.utils.NODE_USERS
import com.example.clontelegram.utils.REF_DATABASE_ROOT
import com.example.clontelegram.utils.CURRENT_UID
import com.example.clontelegram.utils.USER
import com.example.clontelegram.utils.showToast


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
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    showToast(getString(R.string.toast_data_update))
                    USER.bio = newBio
                    fragmentManager?.popBackStack()

                }
            }
    }



}