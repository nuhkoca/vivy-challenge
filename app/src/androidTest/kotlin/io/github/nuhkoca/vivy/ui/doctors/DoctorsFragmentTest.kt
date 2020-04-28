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
package io.github.nuhkoca.vivy.ui.doctors

import BaseTestClass
import android.content.Context
import android.view.LayoutInflater
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import io.github.nuhkoca.vivy.databinding.DoctorsFragmentBinding
import org.junit.Test
import org.junit.runner.RunWith

/*
 * Cannot test this class along with FragmentFactory and navGraphViewModels delegation. If I don't
 * use FragmentFactory and init viewModel with viewModels delegations I am able to test. However,
 * I didn't want to change the whole logic.
 *
 * Issue tracker: https://issuetracker.google.com/issues/153364901
 */

/**
 * A test class for [DoctorsFragment]
 */
@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
class DoctorsFragmentTest : BaseTestClass() {

    @Test
    @UiThreadTest
    fun testFragmentLaunchesAndViewsAreInCorrectVisibility() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val layoutInflater = LayoutInflater.from(context)
        val binding = DoctorsFragmentBinding.inflate(layoutInflater)

        launchDoctorsFragment {
            verifyFirstLunch(binding)
        }
    }
}
