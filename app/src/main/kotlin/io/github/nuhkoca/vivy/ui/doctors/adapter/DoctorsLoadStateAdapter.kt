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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.nuhkoca.vivy.databinding.LayoutLoadStateItemBinding
import io.github.nuhkoca.vivy.ui.di.MainScope
import io.github.nuhkoca.vivy.util.event.SingleLiveEvent
import io.github.nuhkoca.vivy.util.recyclerview.BaseViewHolder
import io.github.nuhkoca.vivy.util.recyclerview.LoadState
import javax.inject.Inject

@MainScope
class DoctorsLoadStateAdapter @Inject constructor(
    private val retryLiveData: SingleLiveEvent<Unit>
) : RecyclerView.Adapter<DoctorsLoadStateAdapter.LoadStateViewHolder>() {

    /**
     * LoadState to present in the adapter.
     *
     * Changing this property will immediately notify the Adapter to change the item it's
     * presenting.
     */
    var loadState: LoadState = LoadState.Done
        set(loadState) {
            if (field != loadState) {
                val displayOldItem = displayLoadStateAsItem(field)
                val displayNewItem = displayLoadStateAsItem(loadState)

                if (displayOldItem && !displayNewItem) {
                    notifyItemRemoved(0)
                } else if (displayNewItem && !displayOldItem) {
                    notifyItemInserted(0)
                } else if (displayOldItem && displayNewItem) {
                    notifyItemChanged(0)
                }
                field = loadState
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = LayoutLoadStateItemBinding.inflate(inflater, parent, false)
        return LoadStateViewHolder(view.root)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, position: Int) {
        holder.bindTo(loadState)
    }

    override fun getItemCount(): Int = if (displayLoadStateAsItem(loadState)) 1 else 0

    /**
     * Returns true if the LoadState should be displayed as a list item when active.
     *
     *  [LoadState.Loading] and [LoadState.Error] present as list items,
     * [LoadState.Done] is not.
     */
    private fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }

    inner class LoadStateViewHolder(itemView: View) :
        BaseViewHolder<LayoutLoadStateItemBinding, LoadState>(itemView) {

        override fun bindTo(item: LoadState) {
            if (item is LoadState.Error) {
                binding.tvError.text = item.error
            }

            binding.pbError.isVisible = item is LoadState.Loading
            binding.btnError.isVisible = item is LoadState.Error
            binding.tvError.isVisible = item is LoadState.Error

            binding.btnError.setOnClickListener {
                retryLiveData.call()
            }
            super.bindTo(item)
        }
    }
}
