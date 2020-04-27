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
@file:JvmName("ActivityKt")

package io.github.nuhkoca.vivy.util.ext

import android.app.Activity
import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import io.github.nuhkoca.vivy.R

/**
 * Starts [OssLicensesMenuActivity] to list all of open source libraries used in the application.
 */
fun Activity.startLicenseActivity() {
    OssLicensesMenuActivity.setActivityTitle(getString(R.string.menu_library_item_title))
    startActivity(Intent(this, OssLicensesMenuActivity::class.java))
}
