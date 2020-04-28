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
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.domain.repository.Repository
import io.github.nuhkoca.vivy.ui.di.MainScope
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.event.SingleLiveEvent
import io.github.nuhkoca.vivy.util.navigation.DetailContract
import io.github.nuhkoca.vivy.util.recyclerview.LoadState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@MainScope
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class DoctorsViewModel @Inject constructor(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    @ExperimentalCoroutinesApi
    val queryChannel = ConflatedBroadcastChannel(EMPTY_QUERY)

    private val queryLiveData = MutableLiveData<String>()
    private val trigger = SingleLiveEvent<Unit>()

    init {
        trigger.call() // Just for triggering the repository

        queryChannel.asFlow()
            .debounce(DEBOUNCE_IN_MS)
            .onEach { query -> queryLiveData.value = query }
            .distinctUntilChanged()
            .launchIn(viewModelScope)
    }

    private val _doctorLiveData = MutableLiveData<DoctorViewItem>()
    val doctorLiveData: LiveData<DoctorViewItem> = _doctorLiveData

    private val _navigationLiveData = SingleLiveEvent<Int>()
    val navigationLiveData: LiveData<Int> = _navigationLiveData

    private val repoResult = trigger.map { repository.getDoctorList() }
    val doctors = repoResult.switchMap { result ->
        queryLiveData.switchMap { query ->
            if (!query.isNullOrBlank()) {
                repository.getDoctorsByName("%$query%")
            } else {
                result.pagedList
            }
        }
    }

    val networkState: LiveData<LoadState> = repoResult.switchMap { it.networkState }

    val recentDoctors = queryLiveData.switchMap { query ->
        repository.getRecentDoctors("%$query%")
    }

    fun retry() = repoResult.value?.retry?.invoke()

    fun setSelectedDoctor(doctorViewItem: DoctorViewItem) {
        viewModelScope.launch(dispatcherProvider.io) {
            _doctorLiveData.postValue(doctorViewItem)
            repository.updateVisitingTimeById(doctorViewItem.id)
        }
    }

    fun navigate() {
        _navigationLiveData.value = DetailContract.actionId
    }

    private companion object {
        private const val EMPTY_QUERY = ""
        private const val DEBOUNCE_IN_MS = 1500L
    }
}
