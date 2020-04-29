/*
 * Copyright (C) 2020. Nuh Koca. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.nuhkoca.vivy.ui.doctors.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.databinding.LayoutDoctorItemBinding
import io.github.nuhkoca.vivy.databinding.LayoutDoctorItemTitleBinding
import io.github.nuhkoca.vivy.ui.di.MainScope
import io.github.nuhkoca.vivy.util.event.SingleLiveEvent
import io.github.nuhkoca.vivy.util.recyclerview.AdapterDataObserverProxy
import io.github.nuhkoca.vivy.util.recyclerview.BaseViewHolder
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_CALL
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_EMAIL
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_MAP
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_WEBSITE
import javax.inject.Inject

@MainScope
class DoctorsAdapter @Inject constructor(
    private val itemClickLiveData: SingleLiveEvent<DoctorViewItem>,
    private val menuClickLiveData: SingleLiveEvent<MenuItem>
) : PagedListAdapter<DoctorViewItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val inflater = LayoutInflater.from(parent.context)
                val view = LayoutDoctorItemTitleBinding.inflate(inflater, parent, false)
                HeaderViewHolder(view.root)
            }
            TYPE_ITEM -> {
                val inflater = LayoutInflater.from(parent.context)
                val view = LayoutDoctorItemBinding.inflate(inflater, parent, false)
                DoctorViewHolder(view.root)
            }
            else -> throw IllegalStateException("No view type found for $viewType")
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DoctorViewHolder -> getItem(position - 1)?.let(holder::bindTo)
        }
    }

    /**
     * Registers [AdapterDataObserverProxy] to insert header for the adapter
     */
    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(AdapterDataObserverProxy(observer, 1))
    }

    /**
     * Unregisters [AdapterDataObserverProxy]
     */
    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(AdapterDataObserverProxy(observer, 1))
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    inner class HeaderViewHolder(itemView: View) :
        BaseViewHolder<LayoutDoctorItemTitleBinding, String>(itemView) {

        init {
            val context = itemView.context
            binding.tvTitle.text = context.getString(R.string.doctors_adapter_header_text)
            binding.executePendingBindings()
        }
    }

    inner class DoctorViewHolder(itemView: View) :
        BaseViewHolder<LayoutDoctorItemBinding, DoctorViewItem>(itemView),
        View.OnCreateContextMenuListener {

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun bindTo(item: DoctorViewItem) {
            binding.doctor = item
            itemView.setOnClickListener { itemClickLiveData.value = item }
            super.bindTo(item)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.setHeaderTitle(R.string.context_menu_header_title)
            menu.add(R.string.context_menu_option_map).setOnMenuItemClickListener {
                menuClickLiveData.value =
                    MenuItem(
                        ITEM_ID_MAP,
                        getItem(bindingAdapterPosition - 1)?.location
                    )
                true
            }
            menu.add(R.string.context_menu_option_call).setOnMenuItemClickListener {
                menuClickLiveData.value =
                    MenuItem(
                        ITEM_ID_CALL,
                        getItem(bindingAdapterPosition - 1)?.phoneNumber
                    )
                true
            }
            menu.add(R.string.context_menu_option_email).setOnMenuItemClickListener {
                menuClickLiveData.value =
                    MenuItem(
                        ITEM_ID_EMAIL,
                        getItem(bindingAdapterPosition - 1)?.email
                    )
                true
            }
            menu.add(R.string.context_menu_option_website).setOnMenuItemClickListener {
                menuClickLiveData.value =
                    MenuItem(
                        ITEM_ID_WEBSITE,
                        getItem(bindingAdapterPosition - 1)?.website
                    )
                true
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DoctorViewItem>() {

            override fun areItemsTheSame(
                oldItem: DoctorViewItem,
                newItem: DoctorViewItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DoctorViewItem,
                newItem: DoctorViewItem
            ): Boolean {
                return oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.photoId == newItem.photoId &&
                    oldItem.rating == newItem.rating &&
                    oldItem.address == newItem.address &&
                    oldItem.location == newItem.location &&
                    oldItem.phoneNumber == newItem.phoneNumber &&
                    oldItem.email == newItem.email &&
                    oldItem.website == newItem.website
            }
        }
    }
}
