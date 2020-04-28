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

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import io.github.nuhkoca.vivy.data.model.Listing
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem

/**
 * A helper interface for repository layer to interact with [DataSource]
 */
interface Repository {

    /**
     * Returns a Listing for all doctors
     *
     * @return result in [Listing] wrapper
     */
    fun getDoctorList(): Listing<DoctorViewItem>

    /**
     * Returns list of doctors with given name constraint
     *
     * @param name The doctor name
     *
     * @return result in [LiveData] wrapper
     */
    fun getDoctorsByName(name: String): LiveData<PagedList<DoctorViewItem>>

    /**
     * Returns recent visited doctors by the most recent visiting time
     *
     * @param name The doctor name
     *
     * @return [DoctorViewItem] in LiveData wrapper
     */
    fun getRecentDoctors(name: String): LiveData<List<DoctorViewItem>>

    /**
     * Updates visiting time of selected doctor
     *
     * @param id The doctor id
     */
    suspend fun updateVisitingTimeById(id: String)
}
