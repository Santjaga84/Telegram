package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clontelegram.MainActivity
import com.example.clontelegram.acivities.RegisterActivity
import com.example.clontelegram.databinding.FragmentEnterCodeBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.AUTH
import com.example.clontelegram.utils.AppTextWatcher
import com.example.clontelegram.utils.CHILD_ID
import com.example.clontelegram.utils.CHILD_PHONE
import com.example.clontelegram.utils.CHILD_USERNAME
import com.example.clontelegram.utils.NODE_USERS
import com.example.clontelegram.utils.REF_DATABASE_ROOT
import com.example.clontelegram.utils.replaceActivity
import com.example.clontelegram.utils.showToast
import com.google.firebase.auth.PhoneAuthProvider

//import kotlinx.android.synthetic.main.fragment_enter_code.registerInputCode


class EnterCodFragment(val phoneNumber: String,val id: String) : Fragment() {

    private lateinit var binding: FragmentEnterCodeBinding  // Объявление переменной привязки

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
       APP_ACTIVITY.title = phoneNumber
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {

            val string: String = binding.registerInputCode.text.toString()
            if (string.length == 6) {
                enterCode()
            }

        })
    }


    private fun enterCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dataMap = mutableMapOf<String,Any>()
                dataMap[CHILD_ID] = uid
                dataMap[CHILD_PHONE] = phoneNumber
                dataMap[CHILD_USERNAME] = uid


                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .updateChildren(dataMap)
                    .addOnCompleteListener{task2 ->
                        if (task2.isSuccessful){
                            showToast("Добро пожаловать")
                            (activity as RegisterActivity).replaceActivity(MainActivity())
                        }else{
                            showToast(task2.exception?.message.toString())
                        }
                }

            } else {
                showToast(task.exception?.message.toString())
            }
        }

    }
}