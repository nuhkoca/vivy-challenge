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
package io.github.nuhkoca.vivy.domain.usecase

import io.github.nuhkoca.vivy.data.Result
import io.github.nuhkoca.vivy.data.model.domain.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.data.succeeded
import io.github.nuhkoca.vivy.domain.repository.Repository
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.mapper.Mapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [UseCase.FlowUseCase] implementation to interact with [Repository] in order to fetch list of
 * currencies.
 *
 * @param repository The repository
 * @param mapper The mapper for UI side
 * @param dispatcherProvider The [DispatcherProvider] to run calls under a specific context
 */
@Singleton
class DoctorsUseCase @Inject constructor(
    private val repository: Repository,
    private val mapper: @JvmSuppressWildcards Mapper<Doctors, DoctorsViewItem>,
    private val dispatcherProvider: DispatcherProvider
) : UseCase.FlowUseCase<DoctorParams, DoctorsViewItem> {

    /**
     * Executes the call with the given parameters.
     *
     * @param params The [DoctorParams] to fetch list
     *
     * @return [DoctorsViewItem] within [Flow] builder
     */
    @ExperimentalCoroutinesApi
    override fun execute(params: DoctorParams): Flow<Result<DoctorsViewItem>> {
        return repository.getDoctorList(params.lastKey)
            .map { result ->
                if (result.succeeded) {
                    result as Result.Success
                    val viewItem = mapper.map(result.data)
                    return@map Result.Success(viewItem)
                }
                result as Result.Error
                return@map Result.Error(result.failure)
            }.flowOn(dispatcherProvider.default)
    }
}

/**
 * The data class to fetch list with last key
 *
 * @property lastKey The key to fetch next pages
 */
data class DoctorParams(
    val lastKey: String? = null
) : Params()
