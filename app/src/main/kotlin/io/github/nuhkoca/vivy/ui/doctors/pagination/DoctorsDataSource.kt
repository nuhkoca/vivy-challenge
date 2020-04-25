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
package io.github.nuhkoca.vivy.ui.doctors.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.nuhkoca.vivy.data.foldSuspend
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.domain.usecase.DoctorParams
import io.github.nuhkoca.vivy.domain.usecase.UseCase
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.ext.e
import io.github.nuhkoca.vivy.util.recyclerview.LoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [PageKeyedDataSource] implementation to fetch list of doctors.
 *
 * @param useCase The use case to fetch and return doctors for UI side
 * @param dispatcherProvider The [DispatcherProvider] to run calls under a specific context
 */
class DoctorsDataSource @Inject constructor(
    private val useCase: @JvmSuppressWildcards UseCase.FlowUseCase<DoctorParams, DoctorsViewItem>,
    private val dispatcherProvider: DispatcherProvider
) : PageKeyedDataSource<String, DoctorViewItem>() {

    private val job = Job()
    private val scope = CoroutineScope(dispatcherProvider.io + job)

    val networkState = MutableLiveData<LoadState>()
    val initialLoad = MutableLiveData<LoadState>()

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, DoctorViewItem>
    ) {
        scope.launch {
            initialLoad.postValue(LoadState.Loading)
            useCase.execute(DoctorParams())
                .collect { result ->
                    result.foldSuspend({
                        initialLoad.postValue(LoadState.Done)
                        callback.onResult(it.doctors, null, it.lastKey)
                    }, {
                        initialLoad.postValue(LoadState.Error(it))
                        e { it.message }
                    })
                }
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, DoctorViewItem>
    ) {
        scope.launch {
            networkState.postValue(LoadState.Loading)
            useCase.execute(DoctorParams(params.key))
                .collect { result ->
                    result.foldSuspend({
                        networkState.postValue(LoadState.Done)
                        callback.onResult(it.doctors, it.lastKey)
                    }, {
                        networkState.postValue(LoadState.Error(it))
                        e { it.message }
                    })
                }
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, DoctorViewItem>
    ) {
        // no-op
    }

    override fun invalidate() {
        super.invalidate()
        job.cancel()
    }
}
