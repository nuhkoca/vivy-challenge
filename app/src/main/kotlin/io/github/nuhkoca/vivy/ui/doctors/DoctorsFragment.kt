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
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.databinding.DoctorsFragmentBinding
import io.github.nuhkoca.vivy.ui.di.MainScope
import io.github.nuhkoca.vivy.ui.doctors.adapter.DoctorsAdapter
import io.github.nuhkoca.vivy.ui.doctors.adapter.DoctorsLoadStateAdapter
import io.github.nuhkoca.vivy.util.event.SingleLiveEvent
import io.github.nuhkoca.vivy.util.ext.composeEmail
import io.github.nuhkoca.vivy.util.ext.dialPhoneNumber
import io.github.nuhkoca.vivy.util.ext.linearLayoutManager
import io.github.nuhkoca.vivy.util.ext.openWebPage
import io.github.nuhkoca.vivy.util.ext.showMap
import io.github.nuhkoca.vivy.util.ext.viewBinding
import io.github.nuhkoca.vivy.util.recyclerview.LoadState
import javax.inject.Inject

/*
 * Cannot test this class along with FragmentFactory and navGraphViewModels delegation. If I don't
 * use FragmentFactory and init viewModel with viewModels delegations I am able to test. However,
 * I didn't want to change the whole logic.
 *
 * Issue tracker: https://issuetracker.google.com/issues/153364901
 */
@MainScope
class DoctorsFragment @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val doctorsAdapter: DoctorsAdapter,
    private val loadStateAdapter: DoctorsLoadStateAdapter,
    private val itemClickLiveData: SingleLiveEvent<DoctorViewItem>,
    private val retryLiveData: SingleLiveEvent<Unit>
) : Fragment(R.layout.doctors_fragment) {

    private val mergeAdapter = MergeAdapter(doctorsAdapter, loadStateAdapter)
    private val binding: DoctorsFragmentBinding by viewBinding(DoctorsFragmentBinding::bind)
    private val mergedBinding by viewBinding { binding.errorContainer }
    private val viewModel: DoctorsViewModel by navGraphViewModels(R.id.nav_graph_main) { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSwipeRefreshLayout()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupSwipeRefreshLayout() = with(binding.srlDoctors) {
        setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark)
        setOnRefreshListener { viewModel.retry() }
    }

    private fun setupRecyclerView() = with(binding.rvDoctors) {
        setHasFixedSize(true); adapter = mergeAdapter
        itemClickLiveData.observe(viewLifecycleOwner) { doctor ->
            viewModel.setSelectedDoctor(doctor)
            viewModel.navigate()
        }
        retryLiveData.observe(viewLifecycleOwner) { viewModel.retry() }
    }

    private fun observeViewModel() = with(viewModel) {
        doctorsLiveData.observe(viewLifecycleOwner) {
            doctorsAdapter.submitList(it) {
                // Workaround for an issue where RecyclerView incorrectly uses the loading / spinner
                // item added to the end of the list as an anchor during initial load.
                // credit: https://github.com/android/architecture-components-samples/blob/master/PagingWithNetworkSample/app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/ui/RedditActivity.kt
                val pos =
                    binding.rvDoctors.linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (pos != RecyclerView.NO_POSITION) {
                    binding.rvDoctors.post { binding.rvDoctors.scrollToPosition(0) }
                }
            }
        }
        networkState.observe(viewLifecycleOwner, loadStateAdapter::loadState::set)
        initialState.observe(viewLifecycleOwner) { state ->
            binding.srlDoctors.isRefreshing = false
            binding.pbDoctors.isVisible = state is LoadState.Loading
            binding.rvDoctors.isVisible = state is LoadState.Done
            mergedBinding.root.isVisible = state is LoadState.Error
        }
        navigationLiveData.observe(viewLifecycleOwner, findNavController()::navigate)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            DoctorsAdapter.ITEM_ID_MAP -> {
                val location = doctorsAdapter.getLocationOf(item.groupId)
                showMap(location)
                true
            }
            DoctorsAdapter.ITEM_ID_CALL -> {
                val number = doctorsAdapter.getPhoneOf(item.groupId)
                dialPhoneNumber(number)
                true
            }
            DoctorsAdapter.ITEM_ID_EMAIL -> {
                val address = doctorsAdapter.getEmailOf(item.groupId)
                composeEmail(address)
                true
            }
            DoctorsAdapter.ITEM_ID_WEBSITE -> {
                val website = doctorsAdapter.getWebsiteOf(item.groupId)
                openWebPage(website)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}
