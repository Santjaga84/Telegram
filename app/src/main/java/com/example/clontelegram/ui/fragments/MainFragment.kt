package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clontelegram.databinding.FragmentChatBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.hideKeyboard

/* Главный фрагмент, содержит все чаты, группы и каналы с которыми взаимодействует пользователь*/
class MainFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding  // Объявление переменной привязки

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Используйте View Binding для надува разметки
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.mAppDriver.enableDrawer()
        hideKeyboard()
    }
}