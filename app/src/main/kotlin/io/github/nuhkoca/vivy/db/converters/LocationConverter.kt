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
package io.github.nuhkoca.vivy.db.converters

import androidx.room.TypeConverter
import io.github.nuhkoca.vivy.data.model.Location
import io.github.nuhkoca.vivy.data.model.inline.Latitude
import io.github.nuhkoca.vivy.data.model.inline.Longitude

/**
 * Type converters for [Location] to allow Room to reference.
 */
class LocationConverter {

    /**
     * Converts [Location] type to corresponding [String]
     *
     * @param value The [Location]
     *
     * @return coordinates in [String]
     */
    @TypeConverter
    fun locationToString(value: Location): String =
        "${value.latitude.value},${value.longitude.value}"

    /**
     * Converts [String] type to corresponding [Location]
     *
     * @param value The coordinates in [String]
     *
     * @return [Location]
     */
    @TypeConverter
    fun stringToLocation(value: String): Location {
        val coordinates = value.split(",")
        val latitude = coordinates.first()
        val longitude = coordinates.last()

        return Location(Latitude(latitude.toDouble()), Longitude(longitude.toDouble()))
    }
}
