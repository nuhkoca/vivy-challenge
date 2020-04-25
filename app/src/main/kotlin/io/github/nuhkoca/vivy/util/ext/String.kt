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
@file:JvmName("StringKt")

package io.github.nuhkoca.vivy.util.ext

private const val ENDPOINT_PREFIX = "interviews/challenges/android/doctors"
private const val RESPONSE_TYPE_SUFFIX = ".json"

/**
 * Converts given last key to corresponding url form.
 *
 * @param lastKey The key
 *
 * @return The url
 */
fun manipulateUrl(lastKey: String?): String {
    return if (lastKey.isNullOrEmpty()) {
        "$ENDPOINT_PREFIX$RESPONSE_TYPE_SUFFIX"
    } else {
        "$ENDPOINT_PREFIX-$lastKey$RESPONSE_TYPE_SUFFIX"
    }
}
