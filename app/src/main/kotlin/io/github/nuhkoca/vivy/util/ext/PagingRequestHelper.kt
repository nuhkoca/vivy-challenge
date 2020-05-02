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
@file:JvmName("PagingRequestHelperKt")

package io.github.nuhkoca.vivy.util.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.nuhkoca.vivy.util.legacy.PagingRequestHelper
import io.github.nuhkoca.vivy.util.recyclerview.LoadState

/**
 * Takes error message from the report.
 *
 * @param report The [PagingRequestHelper.StatusReport]
 *
 * @return error message
 */
private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

/**
 * Creates network state from the report.
 *
 * @return network state in [LiveData] wrapper
 */
fun PagingRequestHelper.createStatusLiveData(): LiveData<LoadState> {
    val liveData = MutableLiveData<LoadState>(LoadState.Loading)
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(LoadState.Loading)
            report.hasError() -> liveData.postValue(LoadState.Error(getErrorMessage(report)))
            else -> liveData.postValue(LoadState.Done)
        }
    }
    return liveData
}
