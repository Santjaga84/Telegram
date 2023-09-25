package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentChangeNameBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.CHILD_FULLNAME
import com.example.clontelegram.utils.NODE_USERS
import com.example.clontelegram.utils.REF_DATABASE_ROOT
import com.example.clontelegram.utils.CURRENT_UID
import com.example.clontelegram.utils.USER
import com.example.clontelegram.utils.showToast

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private lateinit var binding: FragmentChangeNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.mAppDriver.disableDrawer()
        binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume()  {
        super.onResume()
        setHasOptionsMenu(true)
        initFullName()
    }

    private fun initFullName() = with(binding){
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1){
            settingsInputName.setText(fullnameList[0])
            settingsInputSurname.setText(fullnameList[1])
        }else{
            settingsInputName.setText(fullnameList[0])

        }
    }

    override fun change(): Unit = with(binding) {
        val name = binding.settingsInputName.text.toString()
        val surname = binding.settingsInputSurname.text.toString()
        if (name.isEmpty()){
            showToast(getString(R.string.settings_toast_name_isEmpty))
        }else{
            val fullname = "$name $surname"
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
                .setValue(fullname).addOnCompleteListener {
                    if (it.isSuccessful){
                        showToast(getString(R.string.toast_data_update))
                        USER.fullname = fullname
                        APP_ACTIVITY.mAppDriver.updateHeader()
                        fragmentManager?.popBackStack()
                    }
                }
        }
    }
}