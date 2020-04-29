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
package io.github.nuhkoca.vivy.domain.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.github.nuhkoca.vivy.data.model.Listing
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.data.service.DoctorsService
import io.github.nuhkoca.vivy.db.VivyDB
import io.github.nuhkoca.vivy.domain.boundary.DoctorsBoundaryCallback
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.ext.w
import io.github.nuhkoca.vivy.util.mapper.Mapper
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [Repository] implementation to fetch list of doctors from a single source.
 *
 * @param vivyDB The database
 * @param service The remote service
 * @param mapper The mapper to convert between types
 * @param ioExecutor The executor to execute database processes in the background thread
 * @param dispatcherProvider The dispatcher to execute processes under a specific thread
 */
@Singleton
class DoctorsRepository @Inject constructor(
    private val vivyDB: VivyDB,
    private val service: DoctorsService,
    private val mapper: @JvmSuppressWildcards Mapper<Doctors, DoctorsViewItem>,
    private val ioExecutor: Executor,
    private val dispatcherProvider: DispatcherProvider
) : Repository {

    /**
     * Inserts the response into the database while also assigning position indices to items.
     *
     * @param body The list of [DoctorViewItem]
     */
    private fun insertResultIntoDb(body: List<DoctorViewItem>?) {
        body?.let { doctors ->
            vivyDB.runInTransaction {
                vivyDB.doctorsDao.insertAll(doctors)
            }
        }
    }

    /**
     * Returns a Listing for all doctors
     *
     * @return result in [Listing] wrapper
     */
    override fun getDoctorList(): Listing<DoctorViewItem> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = DoctorsBoundaryCallback(
            service = service,
            mapper = mapper,
            handleResponse = this::insertResultIntoDb,
            ioExecutor = ioExecutor
        )
        // We use toLiveData Kotlin extension function here, also LivePagedListBuilder can be used
        val livePagedList = vivyDB.doctorsDao.getDoctorList().toLiveData(
            pageSize = PAGE_SIZE_DEFAULT,
            boundaryCallback = boundaryCallback
        )

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = { boundaryCallback.helper.retryAllFailed() }
        )
    }

    /**
     * Returns list of doctors with given name constraint
     *
     * @param name The doctor name
     *
     * @return result in [LiveData] wrapper
     */
    @MainThread
    override fun getDoctorsByName(name: String): LiveData<PagedList<DoctorViewItem>> {
        return vivyDB.doctorsDao.getDoctorsByName(name).toLiveData(PAGE_SIZE_DEFAULT)
    }

    /**
     * Returns recent visited doctors by the most recent visiting time
     *
     * @param name The doctor name
     *
     * @return [DoctorViewItem] in LiveData wrapper
     */
    @MainThread
    override fun getRecentDoctors(name: String): LiveData<List<DoctorViewItem>> {
        return vivyDB.doctorsDao.getRecentDoctorList(name)
    }

    /**
     * Updates visiting time of selected doctor. Wrapped with [NonCancellable] context as this
     * job must be done in any case. In the event of user might close the screen before the
     * transaction is done, changes won't be reflected to UI. Therefore this job should be always
     * alive.
     *
     * @param id The doctor id
     */
    @WorkerThread
    override suspend fun updateVisitingTimeById(id: String) {
        withContext(NonCancellable + dispatcherProvider.io) {
            vivyDB.doctorsDao.updateVisitingTimeById(Date(System.currentTimeMillis()), id)
            val count = vivyDB.doctorsDao.getRecentCount()

            if (count > RECENT_DOCTOR_COUNT_MAX) {
                w { "Count is higher than 3 so that oldest item is being deleted from recent list" }
                val items = vivyDB.doctorsDao.getAllRecentDoctors()
                val oldestId = items.last().id
                vivyDB.doctorsDao.removeFromRecent(oldestId)
            }
        }
    }

    private companion object {
        private const val PAGE_SIZE_DEFAULT = 20
        private const val RECENT_DOCTOR_COUNT_MAX = 3
    }
}
