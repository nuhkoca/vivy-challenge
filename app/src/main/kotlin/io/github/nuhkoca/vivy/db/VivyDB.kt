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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.db.converters.Converters
import io.github.nuhkoca.vivy.db.dao.DoctorsDao

@Database(
    entities = [DoctorViewItem::class],
    version = VivyDB.VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VivyDB : RoomDatabase() {

    abstract val doctorsDao: DoctorsDao

    internal companion object {
        internal const val VERSION = 1
    }
}
