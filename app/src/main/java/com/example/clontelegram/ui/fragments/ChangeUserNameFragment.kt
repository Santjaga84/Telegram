package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentChangeUserNameBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.AppValueEventListener
import com.example.clontelegram.utils.CHILD_USERNAME
import com.example.clontelegram.utils.NODE_USERNAMES
import com.example.clontelegram.utils.NODE_USERS
import com.example.clontelegram.utils.REF_DATABASE_ROOT
import com.example.clontelegram.utils.CURRENT_UID
import com.example.clontelegram.utils.USER
import com.example.clontelegram.utils.showToast
import java.util.Locale


class ChangeUserNameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {

    private lateinit var binding: FragmentChangeUserNameBinding

    lateinit var mNewUsername:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        APP_ACTIVITY.mAppDriver.disableDrawer()
        binding = FragmentChangeUserNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() = with(binding) {
        super.onResume()
        setHasOptionsMenu(true)
        settingsInputUsername.setText(USER.username)
    }


    override fun change() = with(binding) {
      mNewUsername = settingsInputUsername.text.toString().toLowerCase(Locale.getDefault())
      if (mNewUsername.isEmpty()){
          showToast("Поле пустое")
      }else{
          REF_DATABASE_ROOT.child(NODE_USERNAMES)
              .addListenerForSingleValueEvent(AppValueEventListener{
                  if (it.hasChild(mNewUsername)){
                      showToast("Такой пользователь существует")
                  }else{
                      changeUserName()
                  }
              })
          }
    }

    private fun changeUserName() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    updateCurrentUserName()
                }
            }
    }

    private fun updateCurrentUserName() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
            .setValue(mNewUsername)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    showToast(getString(R.string.toast_data_update))
                    deleteOldUserName()
                }else{
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deleteOldUserName() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    showToast(getString(R.string.toast_data_update))
                    fragmentManager?.popBackStack()
                    USER.username = mNewUsername
                }else{
                    showToast(it.exception?.message.toString())
                }
            }
    }

}