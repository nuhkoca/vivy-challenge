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
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import io.github.nuhkoca.vivy.LifeCycleTestOwner
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.Stubs.listOfDoctorViewItem
import io.github.nuhkoca.vivy.Stubs.selectedDoctorViewItem
import io.github.nuhkoca.vivy.data.model.Listing
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.domain.repository.Repository
import io.github.nuhkoca.vivy.ext.runBlockingTest
import io.github.nuhkoca.vivy.rule.CoroutinesTestRule
import io.github.nuhkoca.vivy.shared.ext.asPagedList
import io.github.nuhkoca.vivy.util.recyclerview.LoadState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * A test class for [DoctorsViewModel]
 */
@RunWith(MockitoJUnitRunner::class)
@LargeTest
class DoctorsViewModelTest : BaseTestClass() {

    /*
     ------------
    |    Rules   |
     ------------
    */
    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /*
     -------------
    |    Mocks    |
     -------------
    */
    @MockK
    private lateinit var repository: Repository

    @RelaxedMockK
    private lateinit var recentDoctorsObserver: Observer<List<DoctorViewItem>>

    @RelaxedMockK
    private lateinit var doctorObserver: Observer<DoctorViewItem>

    @RelaxedMockK
    private lateinit var networkStateObserver: Observer<LoadState>

    @RelaxedMockK
    private lateinit var navigationObserver: Observer<Int>

    /*
     -------------------------
    |    Private variables    |
     -------------------------
    */
    private lateinit var doctorsViewModel: DoctorsViewModel
    private lateinit var lifeCycleTestOwner: LifeCycleTestOwner

    private val _recentDoctorLiveData = MutableLiveData(listOfDoctorViewItem)
    private val recentDoctorLiveData: LiveData<List<DoctorViewItem>> = _recentDoctorLiveData

    private val _doctorLiveData = listOfDoctorViewItem.asPagedList()
    private val doctorLiveData: LiveData<PagedList<DoctorViewItem>> = _doctorLiveData

    private val _networkStateLiveData = MutableLiveData<LoadState>(LoadState.Done)
    private val networkStateLiveData: LiveData<LoadState> = _networkStateLiveData

    @ExperimentalCoroutinesApi
    override fun setUp() {
        super.setUp()

        lifeCycleTestOwner = LifeCycleTestOwner()
        lifeCycleTestOwner.onCreate()

        every { repository.getDoctorList() } returns Listing(
            doctorLiveData,
            networkStateLiveData
        ) {}

        every { repository.getRecentDoctors(any()) } returns recentDoctorLiveData

        coEvery { repository.updateVisitingTimeById(any()) } just Runs

        doctorsViewModel = DoctorsViewModel(repository, coroutinesTestRule.testDispatcherProvider)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `network state should be fetched`() = coroutinesTestRule.runBlockingTest {
        lifeCycleTestOwner.onResume()

        doctorsViewModel.networkState.observe(lifeCycleTestOwner, networkStateObserver)
        val value = doctorsViewModel.networkState.value

        assertThat(value).isNotNull()
        assertThat(value).isEqualTo(LoadState.Done)

        verify { repository.getDoctorList() }

        confirmVerified(repository)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `recent doctors should be fetched`() = coroutinesTestRule.runBlockingTest {
        // Pause the dispatcher and set query in order to trigger recent doctors
        coroutinesTestRule.testDispatcher.pauseDispatcher()
        doctorsViewModel.queryChannel.offer("")
        coroutinesTestRule.testDispatcher.resumeDispatcher()

        lifeCycleTestOwner.onResume()

        doctorsViewModel.recentDoctors.observe(lifeCycleTestOwner, recentDoctorsObserver)
        val value = doctorsViewModel.recentDoctors.value

        assertThat(value).isNotNull()
        assertThat(value).hasSize(listOfDoctorViewItem.size)

        value?.forEachIndexed { index, doctorViewItem ->
            assertThat(doctorViewItem).isNotNull()
            assertThat(doctorViewItem.id).isEqualTo(listOfDoctorViewItem[index].id)
            assertThat(doctorViewItem.name).isEqualTo(listOfDoctorViewItem[index].name)
            assertThat(doctorViewItem.photoId).isEqualTo(listOfDoctorViewItem[index].photoId)
            assertThat(doctorViewItem.rating).isEqualTo(listOfDoctorViewItem[index].rating)
            assertThat(doctorViewItem.address).isEqualTo(listOfDoctorViewItem[index].address)
            assertThat(doctorViewItem.location).isEqualTo(listOfDoctorViewItem[index].location)
            assertThat(doctorViewItem.phoneNumber).isEqualTo(listOfDoctorViewItem[index].phoneNumber)
            assertThat(doctorViewItem.email).isEqualTo(listOfDoctorViewItem[index].email)
            assertThat(doctorViewItem.website).isEqualTo(listOfDoctorViewItem[index].website)
            assertThat(doctorViewItem.isRecent).isEqualTo(listOfDoctorViewItem[index].isRecent)
        }

        verify(exactly = 1) { repository.getRecentDoctors(any()) }

        confirmVerified(repository)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `selected doctor should be provided`() = coroutinesTestRule.runBlockingTest {
        doctorsViewModel.setSelectedDoctor(selectedDoctorViewItem)

        lifeCycleTestOwner.onResume()

        doctorsViewModel.doctorLiveData.observe(lifeCycleTestOwner, doctorObserver)
        val value = doctorsViewModel.doctorLiveData.value

        assertThat(value).isNotNull()
        assertThat(value?.id).isEqualTo(selectedDoctorViewItem.id)
        assertThat(value?.name).isEqualTo(selectedDoctorViewItem.name)
        assertThat(value?.photoId).isEqualTo(selectedDoctorViewItem.photoId)
        assertThat(value?.rating).isEqualTo(selectedDoctorViewItem.rating)
        assertThat(value?.address).isEqualTo(selectedDoctorViewItem.address)
        assertThat(value?.location).isEqualTo(selectedDoctorViewItem.location)
        assertThat(value?.phoneNumber).isEqualTo(selectedDoctorViewItem.phoneNumber)
        assertThat(value?.email).isEqualTo(selectedDoctorViewItem.email)
        assertThat(value?.website).isEqualTo(selectedDoctorViewItem.website)
        assertThat(value?.isRecent).isEqualTo(selectedDoctorViewItem.isRecent)

        coVerify(exactly = 1) { repository.updateVisitingTimeById(any()) }

        confirmVerified(repository)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `navigation id should be provided`() = coroutinesTestRule.runBlockingTest {
        doctorsViewModel.navigate()

        lifeCycleTestOwner.onResume()

        doctorsViewModel.navigationLiveData.observe(lifeCycleTestOwner, navigationObserver)
        val value = doctorsViewModel.navigationLiveData.value

        assertThat(value).isNotNull()
        assertThat(value).isEqualTo(R.id.action_doctorsFragment_to_doctorDetailFragment)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `query channel should provide the latest query`() = coroutinesTestRule.runBlockingTest {
        // Given
        val query = "This is a sample query"

        // When
        doctorsViewModel.queryChannel.offer(query)

        // Then
        val value = doctorsViewModel.queryChannel.waitForEvent()
        assertThat(value).isNotEmpty()
        assertThat(value).isEqualTo(query)
    }

    /**
     * Waits for the value and returns it accordingly
     *
     * @return [T]
     */
    @ExperimentalCoroutinesApi
    private suspend fun <T> BroadcastChannel<T>.waitForEvent(): T =
        with(openSubscription()) {
            val value = receive()
            cancel()
            value
        }

    override fun tearDown() {
        super.tearDown()
        lifeCycleTestOwner.onDestroy()
    }
}
