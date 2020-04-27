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
package io.github.nuhkoca.vivy.util.ext

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import io.github.nuhkoca.vivy.shared.ENDPOINT_PREFIX
import io.github.nuhkoca.vivy.shared.ENDPOINT_SUFFIX
import io.mockk.mockkStatic
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * A test class for String extensions
 */
@RunWith(MockitoJUnitRunner::class)
@SmallTest
class StringExtTest {

    @Test
    fun `url should not contain last key if it is null`() {
        mockkStatic("io.github.nuhkoca.vivy.util.ext.StringKt")

        // Given
        val lastKey = null

        val url = manipulateUrl(lastKey)

        assertThat(url).isEqualTo("$ENDPOINT_PREFIX$ENDPOINT_SUFFIX")
    }

    @Test
    fun `url should contain last key if it is not null`() {
        mockkStatic("io.github.nuhkoca.vivy.util.ext.StringKt")

        // Given
        val lastKey = "CvQD7gEAAKjcb"

        val url = manipulateUrl(lastKey)

        assertThat(url).isEqualTo("$ENDPOINT_PREFIX-$lastKey$ENDPOINT_SUFFIX")
    }
}
