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
package io.github.nuhkoca.vivy.db

import androidx.paging.DataSource
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.db.converters.DateTimeConverter
import io.github.nuhkoca.vivy.db.converters.LocationConverter
import io.github.nuhkoca.vivy.db.dao.DoctorsDao

/**
 * The Room database to have the Single Source of Truth in order to avoid making network busy
 * all the time. This implementation also provides a flexibility in terms of searching and filtering
 * in case network doesn't support such endpoints. Otherwise search and filter operations won't
 * work as expected. This is because [DataSource] only maps data by page. This means that each page
 * will have its own filter. In addition to that, since there is no endpoint for searching doctors
 * by name, handling this case is being too hard. As a result, using Single Source of Truth is the
 * best case for such scenarios.
 */
@Database(entities = [DoctorViewItem::class], version = VivyDB.VERSION, exportSchema = false)
@TypeConverters(LocationConverter::class, DateTimeConverter::class)
abstract class VivyDB : RoomDatabase() {

    abstract val doctorsDao: DoctorsDao

    internal companion object {
        internal const val VERSION = 1
    }
}
