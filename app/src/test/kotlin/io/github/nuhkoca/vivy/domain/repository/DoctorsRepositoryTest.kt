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
package io.github.nuhkoca.vivy.domain.repository

import BaseTestClass
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import io.github.nuhkoca.vivy.LifeCycleTestOwner
import io.github.nuhkoca.vivy.Stubs.listOfDoctorViewItem
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.data.service.DoctorsService
import io.github.nuhkoca.vivy.db.VivyDB
import io.github.nuhkoca.vivy.ext.runBlockingTest
import io.github.nuhkoca.vivy.rule.CoroutinesTestRule
import io.github.nuhkoca.vivy.shared.ext.getPagedList
import io.github.nuhkoca.vivy.shared.ext.loadAllData
import io.github.nuhkoca.vivy.util.mapper.Mapper
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Executor

/**
 * A test class for [DoctorsRepository]
 */
@RunWith(MockitoJUnitRunner::class)
@MediumTest
class DoctorsRepositoryTest : BaseTestClass() {

    /*
     ------------
    |    Rules   |
     ------------
    */
    @get:Rule
    @ExperimentalCoroutinesApi
    val coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /*
     -------------
    |    Mocks    |
     -------------
    */
    @RelaxedMockK
    private lateinit var listObserver: Observer<List<DoctorViewItem>>

    @RelaxedMockK
    private lateinit var vivyDB: VivyDB

    @RelaxedMockK
    private lateinit var service: DoctorsService

    @RelaxedMockK
    private lateinit var mapper: Mapper<Doctors, DoctorsViewItem>

    /*
     -----------------------
    |    Private members    |
     -----------------------
    */
    private lateinit var repository: Repository
    private lateinit var lifeCycleTestOwner: LifeCycleTestOwner
    private val networkExecutor = Executor { command -> command.run() }

    @ExperimentalCoroutinesApi
    override fun setUp() {
        super.setUp()

        every { vivyDB.doctorsDao.getRecentDoctorList(any()) } answers {
            MutableLiveData(listOfDoctorViewItem.take(3))
        }

        every { vivyDB.doctorsDao.getRecentCount() } returns 3

        repository = DoctorsRepository(
            vivyDB,
            service,
            mapper,
            networkExecutor,
            coroutinesTestRule.testDispatcherProvider
        )

        lifeCycleTestOwner = LifeCycleTestOwner()
        lifeCycleTestOwner.onCreate()
    }

    @Test
    fun `repository should get doctor list`() {
        // Given
        val list = listOfDoctorViewItem

        // When
        repository.getDoctorList()

        lifeCycleTestOwner.onResume()

        val pagedlist = getPagedList(list, lifeCycleTestOwner)
        pagedlist.loadAllData()

        // Then
        assertThat(pagedlist).isNotNull()
        assertThat(pagedlist).hasSize(list.size)
        assertThat(pagedlist).containsExactlyElementsIn(list).inOrder()

        verify(exactly = 1) { vivyDB.doctorsDao.getDoctorList() }
        confirmVerified(vivyDB.doctorsDao)
    }

    @Test
    fun `repository should return recent doctors`() {
        // When
        val recentDoctors = repository.getRecentDoctors("%%")

        lifeCycleTestOwner.onResume()
        recentDoctors.observe(lifeCycleTestOwner, listObserver)

        // Then
        assertThat(recentDoctors.value).isNotNull()
        assertThat(recentDoctors.value).hasSize(listOfDoctorViewItem.take(3).size)

        recentDoctors.value?.forEachIndexed { index, doctorViewItem ->
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

        // Verify
        verify(atLeast = 1) { vivyDB.doctorsDao.getRecentDoctorList(any()) }
        confirmVerified(vivyDB)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `repository should update visiting time by id`() = coroutinesTestRule.runBlockingTest {
        // Given
        val id = "ChIJH6T7dVJOqEcRLkHjhRXX_22"

        // When
        repository.updateVisitingTimeById(id)
        val recentDoctors = repository.getRecentDoctors("%%")

        lifeCycleTestOwner.onResume()
        recentDoctors.observe(lifeCycleTestOwner, listObserver)

        // Then
        assertThat(recentDoctors.value).isNotNull()
        assertThat(recentDoctors.value).hasSize(listOfDoctorViewItem.take(3).size)
        assertThat(recentDoctors.value?.find { it.id == id }?.isRecent).isTrue()

        recentDoctors.value?.filter { it.id == id }?.first { doctorViewItem ->
            val byId = listOfDoctorViewItem.first { it.id == doctorViewItem.id }

            assertThat(doctorViewItem.id).isEqualTo(byId.id)
            assertThat(doctorViewItem.name).isEqualTo(byId.name)
            assertThat(doctorViewItem.photoId).isEqualTo(byId.photoId)
            assertThat(doctorViewItem.rating).isEqualTo(byId.rating)
            assertThat(doctorViewItem.address).isEqualTo(byId.address)
            assertThat(doctorViewItem.location).isEqualTo(byId.location)
            assertThat(doctorViewItem.phoneNumber).isEqualTo(byId.phoneNumber)
            assertThat(doctorViewItem.email).isEqualTo(byId.email)
            assertThat(doctorViewItem.website).isEqualTo(byId.website)
            assertThat(doctorViewItem.isRecent).isEqualTo(byId.isRecent)
            true
        }

        // Verify
        verify(atLeast = 1) { vivyDB.doctorsDao.getRecentDoctorList(any()) }
        verify(atLeast = 1) { vivyDB.doctorsDao.getRecentCount() }

        confirmVerified(vivyDB)
    }

    override fun tearDown() {
        super.tearDown()
        lifeCycleTestOwner.onDestroy()
    }
}
