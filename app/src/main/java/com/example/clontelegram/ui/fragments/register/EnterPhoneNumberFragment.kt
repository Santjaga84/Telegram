package com.example.clontelegram.ui.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentEnterPhoneNumberBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.database.AUTH
import com.example.clontelegram.utils.replaceFragment
import com.example.clontelegram.utils.restartActivity
import com.example.clontelegram.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

/* Фрагмент для ввода номера телефона при регистрации */
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

        /* Callback который возвращает результат верификации */
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                /* Функция срабатывает если верификация уже была произведена,
               * пользователь авторизируется в приложении без потверждения по смс */
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        showToast("Добро пожаловать")
                        restartActivity()
                    }else{
                        showToast(task.exception?.message.toString())
                    }
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                /* Функция срабатывает если верификация не удалась*/
                showToast(p0.message.toString())

            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                /* Функция срабатывает если верификация впервые, и отправлена смс */
                replaceFragment(EnterCodFragment(mPhoneNumber,id))
            }
        }
        binding.registerBtnNext.setOnClickListener {
              sendCode()
        }

    }

    private fun sendCode() {
        /* Функция проверяет поле для ввода номер телефона, если поле пустое выводит сообщение.
         * Если поле не пустое, то начинает процедуру авторизации/ регистрации */
        if (binding.registerInputPhoneNumber.text.toString().isEmpty()){

            showToast(getString(R.string.register_toast_enter_phone))
        }else{
           authUser()

            //replaceFragment(EnterCodFragment())
        }
    }

    private fun authUser() {
        /* Инициализация */
       mPhoneNumber = binding.registerInputPhoneNumber.text.toString()
       PhoneAuthProvider.getInstance().verifyPhoneNumber(
           mPhoneNumber,
           60,
           TimeUnit.SECONDS,
           APP_ACTIVITY,
           mCallback
       )
    }

}