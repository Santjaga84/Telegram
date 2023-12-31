package com.example.clontelegram.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.clontelegram.MainActivity
import com.example.clontelegram.R
import com.example.clontelegram.database.updatePhoneToDatabase
import com.example.clontelegram.models.CommonModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* Файл для хранения утилитарных функции, доступных во всем приложении */
fun showToast(message: String){
    /* Функция показывает сообщение */
    Toast.makeText(APP_ACTIVITY,message, Toast.LENGTH_SHORT).show()

    }

fun restartActivity(){
    /* Функция расширения для AppCompatActivity, позволяет запускать активити */
    val intent = Intent(APP_ACTIVITY,MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack:Boolean = true){
    /* Функция расширения для AppCompatActivity, позволяет устанавливать фрагменты */
    if (addStack){
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.dataContainer,
                fragment)
            .commit()
    }else{
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.dataContainer,
                fragment)
            .commit()
    }

}

//fun Fragment.replaceFragment(fragment: Fragment){
//    /* Функция расширения для Fragment, позволяет устанавливать фрагменты */
//    this.parentFragmentManager.beginTransaction()
//        .addToBackStack(null)
//        .replace(R.id.dataContainer,
//            fragment)
//        .commit()
//}

fun hideKeyboard(){
    /* Функция скрывает клавиатуру */
    val imm: InputMethodManager = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken,0)
}

fun ImageView.downloadAndSetImage(url:String){
    /* Функция раширения ImageView, скачивает и устанавливает картинку*/
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(R.drawable.default_user_icon)
        .into(this)
}

@SuppressLint("Range")
fun initContacts() {
    /* Функция считывает контакты с телефонной книги, хаполняет массив arrayContacts моделями CommonModel */
    if (checkPermission(READ_CONTACTS)){
        val arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.let {
            /* Читаем телефонную книгу пока есть следующие элементы */
            while (it.moveToNext()){
                val fullName = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"),"")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhoneToDatabase(arrayContacts)
    }
}

 fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)

}