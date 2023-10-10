package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentChangeUserNameBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.AppValueEventListener
import com.example.clontelegram.database.NODE_USERNAMES
import com.example.clontelegram.database.REF_DATABASE_ROOT
import com.example.clontelegram.database.CURRENT_UID
import com.example.clontelegram.database.USER
import com.example.clontelegram.database.updateCurrentUserName
import com.example.clontelegram.utils.showToast
import java.util.Locale


/* Фрагмент для изменения username пользователя */
class ChangeUserNameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {

    private lateinit var binding: FragmentChangeUserNameBinding

    lateinit var mNewUserName:String

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
      mNewUserName = settingsInputUsername.text.toString().toLowerCase(Locale.getDefault())
      if (mNewUserName.isEmpty()){
          showToast("Поле пустое")
      }else{
          REF_DATABASE_ROOT.child(NODE_USERNAMES)
              .addListenerForSingleValueEvent(AppValueEventListener{
                  if (it.hasChild(mNewUserName)){
                      showToast("Такой пользователь существует")
                  }else{
                      changeUserName()
                  }
              })
          }
    }

    private fun changeUserName() {
        /* Изменение username в базе данных */
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUserName).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    updateCurrentUserName(mNewUserName)
                }
            }
    }





}