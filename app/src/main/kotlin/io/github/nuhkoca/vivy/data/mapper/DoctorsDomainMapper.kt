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
package io.github.nuhkoca.vivy.data.mapper

import io.github.nuhkoca.vivy.data.model.Location
import io.github.nuhkoca.vivy.data.model.domain.Doctor
import io.github.nuhkoca.vivy.data.model.domain.Doctors
import io.github.nuhkoca.vivy.data.model.inline.Latitude
import io.github.nuhkoca.vivy.data.model.inline.Longitude
import io.github.nuhkoca.vivy.data.model.raw.DoctorsRaw
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.ext.i
import io.github.nuhkoca.vivy.util.mapper.Mapper
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [Mapper] implementation to map [DoctorsRaw] to [Doctors] type.
 *
 * @param dispatcherProvider The [DispatcherProvider] to run calls under a specific context
 */
@Singleton
class DoctorsDomainMapper @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) : Mapper<DoctorsRaw, Doctors> {

    /**
     * A suspend function that maps [DoctorsRaw] to [Doctors] type.
     *
     * @param item The [DoctorsRaw]
     *
     * @return [Doctors]
     */
    override suspend fun map(item: DoctorsRaw) = withContext(dispatcherProvider.default) {
        val doctors = mutableListOf<Doctor>()

        item.doctors.forEach { doctorRaw ->
            with(doctorRaw) {
                val doctor = Doctor(
                    id,
                    name,
                    photoId,
                    rating.round(),
                    address,
                    Location(Latitude(latitude), Longitude(longitude)),
                    highlighted,
                    reviewCount ?: 0,
                    specialityIds,
                    source,
                    phoneNumber,
                    email.orEmpty(),
                    website.orEmpty(),
                    openingHours,
                    integration.orEmpty(),
                    translation.orEmpty()
                )

                doctors.add(doctor)
            }
        }

        if (doctors.isNullOrEmpty().not()) {
            i { "Doctors successfully added" }
        }

        Doctors(doctors, item.lastKey)
    }

    /**
     * Rounds given double value with single decimal
     *
     * @return [Double] with single decimal
     */
    private fun Double?.round(): Double {
        val formatter = DecimalFormat("#.#", DecimalFormatSymbols(Locale.ENGLISH)).apply {
            roundingMode = RoundingMode.HALF_UP
        }
        return formatter.format(this).toDouble()
    }
}
