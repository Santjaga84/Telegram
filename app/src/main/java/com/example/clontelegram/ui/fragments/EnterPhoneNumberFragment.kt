package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clontelegram.MainActivity
import com.example.clontelegram.R
import com.example.clontelegram.acivities.RegisterActivity
import com.example.clontelegram.databinding.FragmentEnterPhoneNumberBinding
import com.example.clontelegram.utils.AUTH
import com.example.clontelegram.utils.replaceActivity
import com.example.clontelegram.utils.replaceFragment
import com.example.clontelegram.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class EnterPhoneNumberFragment : Fragment() {

    private lateinit var binding: FragmentEnterPhoneNumberBinding
    private lateinit var mPhoneNumber:String
    private lateinit var mCallback:PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Используйте View Binding для надува разметки

        binding = FragmentEnterPhoneNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        showToast("Добро пожаловать")
                        (activity as RegisterActivity).replaceActivity(MainActivity())
                    }else{
                        showToast(task.exception?.message.toString())
                    }
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodFragment(mPhoneNumber,id))
            }
        }
        binding.registerBtnNext.setOnClickListener {
              sendCode()
        }

    }

    private fun sendCode() {
        if (binding.registerInputPhoneNumber.text.toString().isEmpty()){

            showToast(getString(R.string.register_toast_enter_phone))
        }else{
           authUser()

            //replaceFragment(EnterCodFragment())
        }
    }

    private fun authUser() {
       mPhoneNumber = binding.registerInputPhoneNumber.text.toString()
       PhoneAuthProvider.getInstance().verifyPhoneNumber(
           mPhoneNumber,
           60,
           TimeUnit.SECONDS,
           activity as RegisterActivity,
           mCallback
       )
    }

}