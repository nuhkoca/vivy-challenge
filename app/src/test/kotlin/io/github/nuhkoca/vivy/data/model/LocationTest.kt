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
package io.github.nuhkoca.vivy.data.model

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import io.github.nuhkoca.vivy.data.model.inline.Latitude
import io.github.nuhkoca.vivy.data.model.inline.Longitude
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * A test class for [Location]
 */
@RunWith(MockitoJUnitRunner::class)
@SmallTest
class LocationTest {

    @Test
    fun `location isNull check should return true`() {
        // Given
        val location = Location(Latitude(0.0), Longitude(Double.NaN))

        // When
        val result = location.isNull

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `location isNull check should return false`() {
        // Given
        val location = Location(Latitude(52.19213), Longitude(13.734))

        // When
        val result = location.isNull

        // Then
        assertThat(result).isFalse()
    }
}
