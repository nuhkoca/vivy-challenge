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

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import io.github.nuhkoca.vivy.Stubs.listOfDoctors
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.util.mapper.Mapper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * A test class for [DoctorsViewItemMapper]
 */
@RunWith(MockitoJUnitRunner::class)
@SmallTest
class DoctorsViewItemMapperTest {

    /*
     -------------------------
    |    Private variables    |
     -------------------------
    */
    private lateinit var mapper: Mapper<Doctors, DoctorsViewItem>

    @Before
    fun setUp() {
        mapper = DoctorsViewItemMapper()
    }

    @Test
    fun `mapper should map raw data to view item type properly`() {
        // Given
        val doctors = listOfDoctors

        // When
        val response = mapper.map(doctors)

        // Then
        assertThat(response).isNotNull()
        assertThat(response.lastKey).isEqualTo(doctors.lastKey)
        assertThat(response.doctors).isNotNull()
        assertThat(response.doctors).hasSize(doctors.doctors.size)
    }
}
