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
@file:JvmName("FragmentKt")

package io.github.nuhkoca.vivy.util.ext

import android.content.Intent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.VivyApplication
import io.github.nuhkoca.vivy.data.model.Location

/**
 * An inline value that takes [Fragment]'s application as [VivyApplication]
 */
inline val Fragment.vivyApplication: VivyApplication
    get() = (requireActivity().application as VivyApplication)

/**
 * Starts email intent
 *
 * @param address The email address
 */
fun Fragment.composeEmail(address: String?) {
    if (address.isNullOrEmpty()) {
        requireView().snackBar(requireContext().getString(R.string.intent_error_email_text))
        return
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "*/*"
        putExtra(Intent.EXTRA_EMAIL, address)
    }
    safeStart(intent)
}

/**
 * Starts map intent
 *
 * @param geoLocation The location to mark on the map
 */
fun Fragment.showMap(geoLocation: Location?) {
    if (geoLocation == null || geoLocation.isNull) {
        requireView().snackBar(requireContext().getString(R.string.intent_error_map_text))
        return
    }
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = ("geo:${geoLocation.latitude.value},${geoLocation.longitude.value}?" +
            "q=${geoLocation.latitude.value},${geoLocation.longitude.value}").toUri()
    }
    safeStart(intent)
}

/**
 * Starts dialer intent
 *
 * @param phoneNumber The phone number
 */
fun Fragment.dialPhoneNumber(phoneNumber: String?) {
    if (phoneNumber.isNullOrEmpty()) {
        requireView().snackBar(requireContext().getString(R.string.intent_error_dial_text))
        return
    }
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }
    safeStart(intent)
}

/**
 * Starts browser intent
 *
 * @param url The url address
 */
fun Fragment.openWebPage(url: String?) {
    if (url.isNullOrEmpty()) {
        requireView().snackBar(requireContext().getString(R.string.intent_error_browser_text))
        return
    }
    val webpage = url.toUri()
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    safeStart(intent)
}

/**
 * Starts intent safely checking whether or not package is available
 *
 * @param intent The intent
 */
private fun Fragment.safeStart(intent: Intent) {
    if (intent.resolveActivity(requireActivity().packageManager) != null) {
        startActivity(
            Intent.createChooser(
                intent,
                requireContext().getString(R.string.intent_chooser_title)
            )
        )
    }
}
