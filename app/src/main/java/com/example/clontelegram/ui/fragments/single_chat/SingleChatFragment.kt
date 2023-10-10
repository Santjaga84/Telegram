package com.example.clontelegram.ui.fragments.single_chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clontelegram.R
import com.example.clontelegram.databinding.FragmentSingleChatBinding
import com.example.clontelegram.databinding.ToolbarInfoBinding
import com.example.clontelegram.models.CommonModel
import com.example.clontelegram.models.UserModel
import com.example.clontelegram.ui.fragments.BaseFragment
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.AppValueEventListener
import com.example.clontelegram.database.CURRENT_UID
import com.example.clontelegram.database.FOLDER_MESSAGE_IMAGE
import com.example.clontelegram.database.FOLDER_PROFILE_IMAGE
import com.example.clontelegram.database.NODE_MESSAGES
import com.example.clontelegram.database.NODE_USERS
import com.example.clontelegram.database.REF_DATABASE_ROOT
import com.example.clontelegram.database.REF_STORAGE_ROOT
import com.example.clontelegram.database.TYPE_TEXT
import com.example.clontelegram.database.USER
import com.example.clontelegram.utils.downloadAndSetImage
import com.example.clontelegram.database.getCommonModel
import com.example.clontelegram.database.getUrlFromStorage
import com.example.clontelegram.database.getUserModel
import com.example.clontelegram.database.putImageToStorage
import com.example.clontelegram.database.putUrlToDatabase
import com.example.clontelegram.database.sendMessage
import com.example.clontelegram.database.setMessageAsImage
import com.example.clontelegram.utils.AppChildEventListener
import com.example.clontelegram.utils.AppTextWatcher
import com.example.clontelegram.utils.showToast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class SingleChatFragment(private var contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {


    private lateinit var binding: FragmentSingleChatBinding
    private lateinit var mListenerInfoToolbar:AppValueEventListener
    private lateinit var mReceivingUser:UserModel
    private lateinit var mToolbarInfo:View
    private lateinit var mRefUser:DatabaseReference
    private lateinit var mRefMessages:DatabaseReference
    private lateinit var mAdapter:SingleChatAdapter
    private lateinit var mRecycleView:RecyclerView
    private lateinit var mMessagesListener:AppChildEventListener
    private var mListMessages = mutableListOf<CommonModel>()
    private var mCountMessage = 15
    private var misScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //APP_ACTIVITY.mAppDriver.disableDrawer()
        binding = FragmentSingleChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecycleView()
     }

    private fun initFields() = with(binding){
        mSwipeRefreshLayout = chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(context)
        chatInputMessage.addTextChangedListener(AppTextWatcher{
            val string = chatInputMessage.text.toString()
            if (string.isEmpty()){
                chatBtnSendMessage.visibility = View.GONE
                chatBtnAttach.visibility = View.VISIBLE
            }else{
                chatBtnSendMessage.visibility = View.VISIBLE
                chatBtnAttach.visibility = View.GONE
            }
        })
        chatBtnAttach.setOnClickListener { attachFile() }
    }

    //функция для отправки файлов
    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(600,600)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecycleView() = with(binding) {
        mRecycleView = chatRecycleView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)

        mRecycleView.adapter = mAdapter
        mRecycleView.setHasFixedSize(true)
        mRecycleView.isNestedScrollingEnabled = false
        mRecycleView.layoutManager = mLayoutManager
        mMessagesListener = AppChildEventListener{
            val message = it.getCommonModel()
            if (mSmoothScrollToPosition){
                mAdapter.addItemToBottom(message){
                    mRecycleView.smoothScrollToPosition(mAdapter.itemCount)
                }
            }else{
                mAdapter.addItemToTop(message){
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        mRefMessages.limitToLast(mCountMessage).addChildEventListener(mMessagesListener)

        mRecycleView.addOnScrollListener(object :RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    misScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (misScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3){
                    updateDate()
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateDate() }
    }

    private fun updateDate() {
        mSmoothScrollToPosition = false
        misScrolling = false
        mCountMessage += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessage).addChildEventListener(mMessagesListener)

    }

    private fun initToolbar() = with(binding){
        APP_ACTIVITY.mAppDriver.disableDrawer()
        mToolbarInfo = APP_ACTIVITY.mToolbar.rootView.findViewById(R.id.toolbarInfo)
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

        chatBtnSendMessage.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else {
                sendMessage(
                    message,
                    contact.id,
                    TYPE_TEXT
                ){
                    chatInputMessage.setText("")
                }
            }
        }
    }
    private fun initInfoToolbar()  {
        if (mReceivingUser.fullname.isEmpty()){
            mToolbarInfo.rootView.findViewById<TextView>(R.id.toolbar_chat_fullname).text = contact.fullname
        }else{
            mToolbarInfo.rootView.findViewById<TextView>(R.id.toolbar_chat_fullname).text = mReceivingUser.fullname
        }
        mToolbarInfo.rootView.findViewById<ImageView>(R.id.toolbar_chat_image).downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.rootView.findViewById<TextView>(R.id.toolbar_chat_status).text = mReceivingUser.state

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) = with(binding) {
        /* Активность которая запускается для получения картинки для фото пользователя */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null){
            val uri = CropImage.getActivityResult(data).uri
            val messageKey = REF_DATABASE_ROOT
                .child(NODE_MESSAGES)
                .child(CURRENT_UID)
                .child(contact.id).push().key.toString()

            val path = REF_STORAGE_ROOT
                .child(FOLDER_MESSAGE_IMAGE)
                .child(messageKey)

            // Загружаем картинку в Storage
            putImageToStorage(uri,path){
                //Получаем URL
                getUrlFromStorage(path){
                  setMessageAsImage(contact.id, it, messageKey)
                    mSmoothScrollToPosition = true
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        mToolbarInfo = APP_ACTIVITY.mToolbar.rootView.findViewById(R.id.toolbarInfo)
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)

    }
}