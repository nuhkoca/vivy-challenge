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
package io.github.nuhkoca.vivy.data.datasource

import io.github.nuhkoca.vivy.data.Result
import io.github.nuhkoca.vivy.data.model.domain.Doctors
import io.github.nuhkoca.vivy.data.model.raw.DoctorsRaw
import io.github.nuhkoca.vivy.data.service.DoctorsService
import io.github.nuhkoca.vivy.util.coroutines.AsyncManager
import io.github.nuhkoca.vivy.util.coroutines.DefaultAsyncManager
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.mapper.Mapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [DataSource] implementation to fetch list of doctors remotely.
 *
 * @param doctorsService The service to hit the endpoint
 * @param mapper The domain mapper to map raw data to domain type
 * @param dispatcherProvider The [DispatcherProvider] to execute calls under a specific context
 */
@Singleton
class DoctorsRemoteDataSource @Inject constructor(
    private val doctorsService: DoctorsService,
    private val mapper: @JvmSuppressWildcards Mapper<DoctorsRaw, Doctors>,
    private val dispatcherProvider: DispatcherProvider
) : DataSource, AsyncManager by DefaultAsyncManager(dispatcherProvider) {

    /**
     * Fetches list of doctors
     *
     * @param lastKey The key to fetch next pages
     *
     * @return [Doctors] within [Flow] builder
     */
    override fun getDoctorList(lastKey: String?): Flow<Result<Doctors>> {
        return handleAsyncWithTryCatch {
            // We should convert last key to correct url form
            val manipulatedUrl = manipulateUrl(lastKey)
            val response = doctorsService.getDoctorList(manipulatedUrl)
            mapper.map(response)
        }
    }

    private companion object {
        private const val ENDPOINT_PREFIX = "interviews/challenges/android/doctors"
        private const val RESPONSE_TYPE_SUFFIX = ".json"

        private fun manipulateUrl(lastKey: String?): String {
            return if (lastKey.isNullOrEmpty()) {
                "$ENDPOINT_PREFIX$RESPONSE_TYPE_SUFFIX"
            } else {
                "$ENDPOINT_PREFIX-$lastKey$RESPONSE_TYPE_SUFFIX"
            }
        }
    }
}
