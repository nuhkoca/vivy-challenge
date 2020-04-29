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

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.MergeAdapter
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.data.model.Location
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.databinding.DoctorsFragmentBinding
import io.github.nuhkoca.vivy.ui.Searchable
import io.github.nuhkoca.vivy.ui.di.MainScope
import io.github.nuhkoca.vivy.ui.doctors.adapter.DoctorsAdapter
import io.github.nuhkoca.vivy.ui.doctors.adapter.DoctorsLoadStateAdapter
import io.github.nuhkoca.vivy.ui.doctors.adapter.RecentDoctorsAdapter
import io.github.nuhkoca.vivy.util.event.SingleLiveEvent
import io.github.nuhkoca.vivy.util.ext.composeEmail
import io.github.nuhkoca.vivy.util.ext.dialPhoneNumber
import io.github.nuhkoca.vivy.util.ext.openWebPage
import io.github.nuhkoca.vivy.util.ext.showMap
import io.github.nuhkoca.vivy.util.ext.viewBinding
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_CALL
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_EMAIL
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_MAP
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem.Companion.ITEM_ID_WEBSITE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/*
 * Cannot test this class along with FragmentFactory and navGraphViewModels delegation. If I don't
 * use FragmentFactory and init viewModel with viewModels delegations I am able to test. However,
 * I didn't want to change the whole logic.
 *
 * Issue tracker: https://issuetracker.google.com/issues/153364901
 */
@MainScope
@Suppress("LongParameterList")
class DoctorsFragment @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val doctorsAdapter: DoctorsAdapter,
    private val recentDoctorsAdapter: RecentDoctorsAdapter,
    private val loadStateAdapter: DoctorsLoadStateAdapter,
    private val itemClickLiveData: SingleLiveEvent<DoctorViewItem>,
    private val retryLiveData: SingleLiveEvent<Unit>,
    private val menuClickLiveData: SingleLiveEvent<MenuItem>
) : Fragment(R.layout.doctors_fragment), Searchable {

    private val mergeAdapter = MergeAdapter(recentDoctorsAdapter, doctorsAdapter, loadStateAdapter)
    private val binding: DoctorsFragmentBinding by viewBinding(DoctorsFragmentBinding::bind)
    private val viewModel: DoctorsViewModel by navGraphViewModels(R.id.nav_graph_main) { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() = with(binding.rvDoctors) {
        setHasFixedSize(true); adapter = mergeAdapter
        itemClickLiveData.observe(viewLifecycleOwner) { doctor ->
            viewModel.setSelectedDoctor(doctor)
            viewModel.navigate()
        }
        retryLiveData.observe(viewLifecycleOwner) { viewModel.retry() }
        menuClickLiveData.observe(viewLifecycleOwner) {
            when (it.id) {
                ITEM_ID_MAP -> showMap(it.item as Location)
                ITEM_ID_CALL -> dialPhoneNumber(it.item.toString())
                ITEM_ID_EMAIL -> composeEmail(it.item.toString())
                ITEM_ID_WEBSITE -> openWebPage(it.item.toString())
            }
        }
    }

    private fun observeViewModel() = with(viewModel) {
        doctors.observe(viewLifecycleOwner, doctorsAdapter::submitList)
        recentDoctors.observe(viewLifecycleOwner, recentDoctorsAdapter::submitList)
        networkState.observe(viewLifecycleOwner, loadStateAdapter::loadState::set)
        navigationLiveData.observe(viewLifecycleOwner, findNavController()::navigate)
    }

    @ExperimentalCoroutinesApi
    override fun onQueryChange(query: String) {
        viewModel.queryChannel.offer(query)
    }
}
