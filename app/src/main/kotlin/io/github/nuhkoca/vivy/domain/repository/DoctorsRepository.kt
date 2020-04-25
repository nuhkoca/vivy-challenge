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

import io.github.nuhkoca.vivy.data.Result
import io.github.nuhkoca.vivy.data.datasource.DataSource
import io.github.nuhkoca.vivy.data.model.domain.Doctors
import io.github.nuhkoca.vivy.di.Remote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [Repository] implementation to interact with [DataSource] in order to fetch list of
 * doctors.
 *
 * @param remoteDataSource The data source
 */
@Singleton
class DoctorsRepository @Inject constructor(
    @Remote private val remoteDataSource: DataSource
) : Repository {

    /**
     * Fetches list of doctors
     *
     * @param lastKey The key to fetch next pages
     *
     * @return [Doctors] within [Flow] builder
     */
    override fun getDoctorList(lastKey: String?): Flow<Result<Doctors>> {
        return remoteDataSource.getDoctorList(lastKey)
    }
}
