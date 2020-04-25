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
import kotlinx.coroutines.flow.Flow

/**
 * A common interface for children data sources to fetch list of doctors.
 */
interface DataSource {

    /**
     * Fetches list of doctors
     *
     * @param lastKey The key to fetch next pages
     *
     * @return [Doctors] within [Flow] builder
     */
    fun getDoctorList(lastKey: String?): Flow<Result<Doctors>>
}
