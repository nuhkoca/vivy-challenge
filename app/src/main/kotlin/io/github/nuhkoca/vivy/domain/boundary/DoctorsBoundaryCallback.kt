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
package io.github.nuhkoca.vivy.domain.boundary

import androidx.annotation.MainThread
import androidx.paging.PagedList
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.data.service.DoctorsService
import io.github.nuhkoca.vivy.util.ext.createStatusLiveData
import io.github.nuhkoca.vivy.util.ext.e
import io.github.nuhkoca.vivy.util.ext.manipulateUrl
import io.github.nuhkoca.vivy.util.legacy.PagingRequestHelper
import io.github.nuhkoca.vivy.util.mapper.Mapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the [PagingRequestHelper] class.
 */
class DoctorsBoundaryCallback(
    private val service: DoctorsService,
    private val mapper: Mapper<Doctors, DoctorsViewItem>,
    private val handleResponse: (List<DoctorViewItem>?) -> Unit,
    private val ioExecutor: Executor
) : PagedList.BoundaryCallback<DoctorViewItem>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    private var lastKey: String? = null

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            val url = manipulateUrl(lastKey)
            service.getDoctorList(url).enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: DoctorViewItem) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            if (!lastKey.isNullOrEmpty()) {
                val url = manipulateUrl(lastKey)
                service.getDoctorList(url).enqueue(createWebserviceCallback(it))
            } else {
                it.recordSuccess()
            }
        }
    }

    /**
     * Every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
        response: List<DoctorViewItem>,
        it: PagingRequestHelper.Request.Callback
    ) {
        ioExecutor.execute {
            handleResponse(response)
            it.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: DoctorViewItem) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback): Callback<Doctors> {
        return object : Callback<Doctors> {
            override fun onFailure(call: Call<Doctors>, t: Throwable) {
                it.recordFailure(t)
                e { t.message.toString() }
            }

            override fun onResponse(call: Call<Doctors>, response: Response<Doctors>) {
                lastKey = response.body()?.lastKey

                val mapped = mapper.map(response.body())
                insertItemsIntoDb(mapped.doctors, it)
            }
        }
    }
}
