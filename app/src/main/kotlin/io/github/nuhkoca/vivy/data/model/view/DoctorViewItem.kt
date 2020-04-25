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
package io.github.nuhkoca.vivy.data.model.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.nuhkoca.vivy.data.model.Location
import java.util.*

/**
 * A data class that represents each doctor for UI and database side
 *
 * @property id The doctor id
 * @property name The name of doctor
 * @property photoId The photo of doctor
 * @property rating The rating for doctor
 * @property address The address of doctor
 * @property location The location info for doctor venue
 * @property phoneNumber The phone number of doctor
 * @property email The email address of doctor
 * @property website The website of doctor
 * @property recentVisiting The recent visiting date of doctor
 * @property isRecent The flag is doctor has been recently visited
 */
@Entity(tableName = "doctors")
data class DoctorViewItem(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "photo")
    val photoId: String?,
    val rating: Double,
    val address: String,
    val location: Location,
    @ColumnInfo(name = "phone")
    val phoneNumber: String,
    val email: String,
    val website: String,
    @Transient
    @ColumnInfo(name = "recent_visiting")
    val recentVisiting: Date = Date(),
    @Transient
    @ColumnInfo(name = "is_recent")
    val isRecent: Boolean = false
)
