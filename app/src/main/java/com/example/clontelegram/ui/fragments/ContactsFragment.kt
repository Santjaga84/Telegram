package com.example.clontelegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clontelegram.R
import com.example.clontelegram.databinding.ContactItemBinding
import com.example.clontelegram.databinding.FragmentContactsBinding
import com.example.clontelegram.models.CommonModel
import com.example.clontelegram.ui.fragments.single_chat.SingleChatFragment
import com.example.clontelegram.utils.APP_ACTIVITY
import com.example.clontelegram.utils.AppValueEventListener
import com.example.clontelegram.database.CURRENT_UID
import com.example.clontelegram.database.NODE_PHONES_CONTACTS
import com.example.clontelegram.database.NODE_USERS
import com.example.clontelegram.database.REF_DATABASE_ROOT
import com.example.clontelegram.utils.downloadAndSetImage
import com.example.clontelegram.database.getCommonModel
import com.example.clontelegram.utils.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class ContactsFragment : BaseFragment(R.layout.fragment_contacts){

    private lateinit var binding: FragmentContactsBinding

    private lateinit var mRecycleView:RecyclerView
    private lateinit var mAdapter:FirebaseRecyclerAdapter<CommonModel,ContactsHolder>
    private lateinit var mRefContacts:DatabaseReference
    private lateinit var mRefUsers:DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //APP_ACTIVITY.mAppDriver.disableDrawer()
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
        initRecycleView()
    }

    private fun initRecycleView() = with(binding) {
        mRecycleView = contactsRecycleView
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)

        //Настройка для адаптера, где указываем какие данные и откуда получать
        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts,CommonModel::class.java)
            .build()

        //Адаптер принимает данные, отображает в холдере
        mAdapter = object :FirebaseRecyclerAdapter<CommonModel,ContactsHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                //Запускается тогда когда адаптер получает доступ к ViewGroup
                val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
                return ContactsHolder(binding)
            }

            //Заполняет холдер
            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                mRefUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()
                    if (contact.fullname.isEmpty()){
                        holder.name.text = model.fullname
                    }else{
                        holder.name.text = contact.fullname
                    }

                    holder.status.text = contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener {
                       replaceFragment(SingleChatFragment(model))
                    }
                }

                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers] = mRefUsersListener


            }
        }

        mRecycleView.adapter = mAdapter
        mAdapter.startListening()
    }

    //Холдер для захвата ViewGroup
    class ContactsHolder(binding: ContactItemBinding):RecyclerView.ViewHolder(binding.root){

        val name:TextView = binding.contactFullname
        val status:TextView = binding.contactStatus
        val photo:CircleImageView = binding.contactPhoto
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()

        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }
    }
}


