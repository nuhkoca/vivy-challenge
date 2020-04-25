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

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.api.load
import coil.transform.CircleCropTransformation
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.binding.di.BindingScope
import javax.inject.Inject

/**
 * A [BindingAdapter] for ImageView processes.
 *
 * @property imageLoader The Coil loader
 */
@BindingScope
class ImageBindingAdapter @Inject constructor(private val imageLoader: ImageLoader) {

    /**
     * Binds image to target ImageView
     *
     * @param url The image url
     */
    @BindingAdapter("android:src")
    fun ImageView.bindImage(url: String?) {
        load(url, imageLoader) {
            fallback(R.drawable.ic_error)
            transformations(CircleCropTransformation())
            size(IMAGE_WIDTH_SIZE_DEFAULT, IMAGE_HEIGHT_SIZE_DEFAULT)
            build()
        }
    }

    private companion object {
        private const val IMAGE_WIDTH_SIZE_DEFAULT = 200
        private const val IMAGE_HEIGHT_SIZE_DEFAULT = 200
    }
}
