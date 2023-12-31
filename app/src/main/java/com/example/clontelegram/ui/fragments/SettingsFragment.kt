package com.example.clontelegram.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentSettingsBinding
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.database.AUTH
import com.example.clontelegram.utils.AppStates
import com.example.clontelegram.database.CURRENT_UID
import com.example.clontelegram.database.FOLDER_PROFILE_IMAGE
import com.example.clontelegram.database.REF_STORAGE_ROOT
import com.example.clontelegram.database.USER
import com.example.clontelegram.utils.downloadAndSetImage
import com.example.clontelegram.database.getUrlFromStorage
import com.example.clontelegram.database.putImageToStorage
import com.example.clontelegram.database.putUrlToDatabase
import com.example.clontelegram.utils.replaceFragment
import com.example.clontelegram.utils.restartActivity
import com.example.clontelegram.utils.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


/* Фрагмент настроек */
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding  // Объявление переменной привязки

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Используйте View Binding для надува разметки
        APP_ACTIVITY.mAppDriver.disableDrawer()

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() = with(binding) {
        settingsBio.text = USER.bio
        settingsFullName.text = USER.fullname
        settingsPhoneNumber.text = USER.phone
        settingsStatus.text = USER.state
        settingsUsername.text = USER.username
        settingsBtnChangeUsername.setOnClickListener {replaceFragment(ChangeUserNameFragment())}
        settingsBtnChangeBio.setOnClickListener {replaceFragment(ChangeBioFragment())}
        settingsChangePhoto.setOnClickListener {changePhotoUser()}
        settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }

    private fun changePhotoUser() {
        /* Изменения фото пользователя */
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(250,250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        /* Создания выпадающего меню*/
        activity?.menuInflater?.inflate(R.menu.settings_actions_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Слушатель выбора пунктов выпадающего меню */
        when(item.itemId){
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) = with(binding) {
        /* Активность которая запускается для получения картинки для фото пользователя */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null){
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)

            // Загружаем картинку в Storage
            putImageToStorage(uri,path){
               //Получаем URL
                getUrlFromStorage(path){

                    //Полученный URL устанавливаем в Child.photoUrl пользователя
                    putUrlToDatabase(it){
                       //выполняем конечные операции(например обновляем фото)
                        settingsUserPhoto.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                        APP_ACTIVITY.mAppDriver.updateHeader()
                   }
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.mAppDriver.enableDrawer()
    }
 }
