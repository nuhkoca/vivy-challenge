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

import androidx.annotation.WorkerThread
import io.github.nuhkoca.vivy.data.model.Location
import io.github.nuhkoca.vivy.data.model.inline.Latitude
import io.github.nuhkoca.vivy.data.model.inline.Longitude
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.util.ext.i
import io.github.nuhkoca.vivy.util.mapper.Mapper
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [Mapper] implementation to map [Doctors] to [DoctorsViewItem] type.
 */
@Singleton
class DoctorsViewItemMapper @Inject constructor() : Mapper<Doctors, DoctorsViewItem> {

    /**
     * Maps [Doctors] to [DoctorsViewItem] type.
     *
     * @param item The [Doctors]
     *
     * @return [DoctorsViewItem]
     */
    @WorkerThread
    override fun map(item: Doctors?): DoctorsViewItem {
        val doctors = mutableListOf<DoctorViewItem>()

        item?.doctors?.forEach { doctor ->
            with(doctor) {
                val viewItem = DoctorViewItem(
                    id,
                    name,
                    photoId,
                    rating.round(),
                    address,
                    Location(Latitude(latitude), Longitude(longitude)),
                    phoneNumber,
                    email.orEmpty(),
                    website.orEmpty()
                )

                doctors.add(viewItem)
            }
        }

        if (doctors.isNullOrEmpty().not()) {
            i { "Doctors successfully added" }
        }

        return DoctorsViewItem(doctors, item?.lastKey)
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
