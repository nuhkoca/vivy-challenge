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
package io.github.nuhkoca.vivy.binding.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.binding.di.BindingScope
import javax.inject.Inject

/**
 * A [BindingAdapter] for TextView processes.
 */
@BindingScope
class TextBindingAdapter @Inject constructor() {

    /**
     * Binds rating to target TextView
     *
     * @param rating The rating value
     */
    @BindingAdapter("android:rating")
    fun TextView.bindRating(rating: Double) {
        text = if (rating.isNaN() || rating == 0.0) {
            context.getString(R.string.not_rated_yet)
        } else {
            rating.toString()
        }
    }
}
