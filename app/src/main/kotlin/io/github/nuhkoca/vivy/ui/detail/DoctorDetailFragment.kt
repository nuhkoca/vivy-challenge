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
package io.github.nuhkoca.vivy.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.navGraphViewModels
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.databinding.FragmentDoctorDetailBinding
import io.github.nuhkoca.vivy.ui.di.MainScope
import io.github.nuhkoca.vivy.ui.doctors.DoctorsViewModel
import javax.inject.Inject

/*
 * Cannot test this class along with FragmentFactory and navGraphViewModels delegation. If I don't
 * use FragmentFactory and init viewModel with viewModels delegations I am able to test. However,
 * I didn't want to change the whole logic.
 *
 * Issue tracker: https://issuetracker.google.com/issues/153364901
 */
@MainScope
class DoctorDetailFragment @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private lateinit var binding: FragmentDoctorDetailBinding
    private val viewModel: DoctorsViewModel by navGraphViewModels(R.id.nav_graph_main) { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoctorDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@DoctorDetailFragment
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() = with(viewModel) {
        doctorLiveData.observe(viewLifecycleOwner) {
            binding.doctor = it
            binding.executePendingBindings()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}
