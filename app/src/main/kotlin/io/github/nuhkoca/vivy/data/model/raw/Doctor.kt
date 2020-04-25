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
package io.github.nuhkoca.vivy.data.model.raw

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class that represents each doctor
 *
 * @property id The doctor id
 * @property name The name of doctor
 * @property photoId The photo of doctor
 * @property rating The rating for doctor
 * @property address The address of doctor
 * @property latitude The latitude point
 * @property longitude The longitude point
 * @property highlighted The flag that indicates whether or not doctor is highlighted
 * @property reviewCount The review count for doctor
 * @property specialityIds The speciality areas of doctor
 * @property source The source for doctor
 * @property phoneNumber The phone number of doctor
 * @property email The email address of doctor
 * @property website The website of doctor
 * @property openingHours The opening hours info for doctor
 * @property integration The integration info for doctor
 * @property translation The translation info for doctor
 */
@Serializable
data class Doctor(
    val id: String,
    val name: String,
    val photoId: String?,
    val rating: Double?,
    val address: String,
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lng")
    val longitude: Double,
    val highlighted: Boolean,
    val reviewCount: Int?,
    val specialityIds: List<Int>,
    val source: String,
    val phoneNumber: String,
    val email: String?,
    val website: String?,
    val openingHours: List<String>,
    val integration: String?,
    val translation: String?
)
