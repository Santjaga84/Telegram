package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentChatBinding
import com.example.clontelegram.databinding.FragmentEnterCodeBinding


class ChatsFragment : Fragment() {

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

    }
}