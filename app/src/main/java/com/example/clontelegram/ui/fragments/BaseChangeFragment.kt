package com.example.clontelegram.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.clontelegram.MainActivity
import com.example.clontelegram.R
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.hideKeyboard


/* Базовый фрагмент, от него наследуются фрагменты где происходит изменение данных о пользователе. */

open class BaseChangeFragment(layout:Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        APP_ACTIVITY.mAppDriver.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        /* Создание выпадающего меню*/
        APP_ACTIVITY.menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Слушатель выбора пункта выпадающего меню */
        when(item.itemId){
            R.id.settings_confirm_change -> change()
        }
        return true

    }

    open fun change() {

    }
}