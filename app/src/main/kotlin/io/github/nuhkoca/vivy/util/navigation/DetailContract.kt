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
package io.github.nuhkoca.vivy.util.navigation

import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.ui.detail.DoctorDetailFragment
import io.github.nuhkoca.vivy.ui.doctors.DoctorsFragment

/**
 * A [Navigateable] implementation for navigation from [DoctorsFragment] to [DoctorDetailFragment]
 */
object DetailContract : Navigateable {

    override val actionId: Int
        get() = R.id.action_doctorsFragment_to_doctorDetailFragment
}
