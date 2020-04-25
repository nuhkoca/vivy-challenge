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
import java.util.*

/**
 * Type converters for [Date] to allow Room to reference.
 */
class DateTimeConverter {

    /**
     * Converts [Date] type to corresponding [Long]
     *
     * @param value The [Date]
     *
     * @return time in millis
     */
    @TypeConverter
    fun dateTimeToLong(value: Date): Long = value.time

    /**
     * Converts [Long] type to corresponding [Date]
     *
     * @param value The millis
     *
     * @return [Date]
     */
    @TypeConverter
    fun longToDateTime(value: Long): Date = Date(value)
}
