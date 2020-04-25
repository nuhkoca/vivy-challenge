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
package io.github.nuhkoca.vivy.domain.mapper

import io.github.nuhkoca.vivy.data.model.domain.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.ext.i
import io.github.nuhkoca.vivy.util.mapper.Mapper
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [Mapper] implementation to map [Doctors] to [DoctorsViewItem] type.
 *
 * @param dispatcherProvider The [DispatcherProvider] to run calls under a specific context
 */
@Singleton
class DoctorsViewItemMapper @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) : Mapper<Doctors, DoctorsViewItem> {

    /**
     * A suspend function that maps [Doctors] to [DoctorsViewItem] type.
     *
     * @param item The [Doctors]
     *
     * @return [DoctorsViewItem]
     */
    override suspend fun map(item: Doctors) = withContext(dispatcherProvider.default) {
        val doctors = mutableListOf<DoctorViewItem>()

        item.doctors.forEach { doctorRaw ->
            with(doctorRaw) {
                val doctor = DoctorViewItem(
                    id,
                    name,
                    photoId,
                    rating,
                    address,
                    location,
                    phoneNumber,
                    email,
                    website
                )

                doctors.add(doctor)
            }
        }

        if (doctors.isNullOrEmpty().not()) {
            i { "Doctors successfully added" }
        }

        DoctorsViewItem(doctors, item.lastKey)
    }
}
