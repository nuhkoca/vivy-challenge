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
package io.github.nuhkoca.vivy.ui.doctors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.ui.di.MainScope
import io.github.nuhkoca.vivy.ui.doctors.pagination.DoctorsDataSourceFactory
import io.github.nuhkoca.vivy.util.event.SingleLiveEvent
import io.github.nuhkoca.vivy.util.navigation.DetailContract
import io.github.nuhkoca.vivy.util.recyclerview.LoadState
import javax.inject.Inject

@MainScope
class DoctorsViewModel @Inject constructor(
    private val factory: DoctorsDataSourceFactory
) : ViewModel() {

    private val _doctorLiveData = MutableLiveData<DoctorViewItem>()
    val doctorLiveData: LiveData<DoctorViewItem> = _doctorLiveData

    private val _navigationLiveData = SingleLiveEvent<Int>()
    val navigationLiveData: LiveData<Int> = _navigationLiveData

    val doctorsLiveData: LiveData<PagedList<DoctorViewItem>> =
        LivePagedListBuilder(factory, config).build()

    val initialState: LiveData<LoadState> = factory.mutableDataSource.switchMap { it.initialLoad }

    val networkState: LiveData<LoadState> = factory.mutableDataSource.switchMap { it.networkState }

    fun retry() = factory.mutableDataSource.value?.invalidate()

    fun setSelectedDoctor(doctorViewItem: DoctorViewItem) {
        _doctorLiveData.value = doctorViewItem
    }

    fun navigate() {
        _navigationLiveData.value = DetailContract.actionId
    }

    private companion object {
        private const val PAGE_SIZE_DEFAULT = 20

        private val config = PagedList.Config.Builder().apply {
            setPageSize(PAGE_SIZE_DEFAULT)
            setEnablePlaceholders(true)
        }.build()
    }
}
