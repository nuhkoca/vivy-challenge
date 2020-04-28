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
package io.github.nuhkoca.vivy.db

import BaseTestClass
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.toLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import io.github.nuhkoca.vivy.rule.CoroutinesTestRule
import io.github.nuhkoca.vivy.LifeCycleTestOwner
import io.github.nuhkoca.vivy.Stubs.listOfDoctorViewItem
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.db.dao.DoctorsDao
import io.github.nuhkoca.vivy.ext.runBlockingTest
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 * A test class for [VivyDB] and [DoctorsDao]
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class VivyDBTest : BaseTestClass() {

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
    private lateinit var observer: Observer<List<DoctorViewItem>>

    /*
     -----------------------
    |    Private members    |
     -----------------------
    */
    private lateinit var doctorsDao: DoctorsDao
    private lateinit var db: VivyDB
    private lateinit var lifeCycleTestOwner: LifeCycleTestOwner

    override fun setUp() {
        super.setUp()

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VivyDB::class.java).build()
        doctorsDao = db.doctorsDao

        lifeCycleTestOwner = LifeCycleTestOwner()
        lifeCycleTestOwner.onCreate()
    }

    @Test
    @Throws(Exception::class)
    @ExperimentalCoroutinesApi
    fun testDatabaseObserveDoctorList() = coroutinesTestRule.runBlockingTest {
        // Given
        val doctors = listOfDoctorViewItem

        // When
        doctorsDao.insertAll(doctors)

        lifeCycleTestOwner.onResume()

        // Then
        val doctorList = doctorsDao.getDoctorList().toLiveData(20)
        doctorList.observe(lifeCycleTestOwner, observer)
        assertThat(doctorList.value).hasSize(doctors.filter { !it.isRecent }.size)
        assertThat(doctorList.value).isEqualTo(doctors.filter { !it.isRecent })
    }

    @Test
    @Throws(Exception::class)
    @ExperimentalCoroutinesApi
    fun testDatabaseObserveDoctorsByName() = coroutinesTestRule.runBlockingTest {
        // Given
        val doctors = listOfDoctorViewItem
        val name = "Gemeinschaftspraxis Dr. Hintsche und Dr. Klausen"

        // When
        doctorsDao.insertAll(doctors)

        lifeCycleTestOwner.onResume()

        // Then
        val doctorList = doctorsDao.getDoctorsByName(name).toLiveData(20)
        doctorList.observe(lifeCycleTestOwner, observer)
        assertThat(doctorList.value).hasSize(doctors.filter { it.name.contains(name) }.size)
        assertThat(doctorList.value).isEqualTo(doctors.filter { it.name.contains(name) })
    }

    @Test
    @Throws(Exception::class)
    fun testDatabaseReturnsCorrectRecentCount() {
        // Given
        val doctors = listOfDoctorViewItem

        // When
        doctorsDao.insertAll(doctors)

        // Then
        val count = doctorsDao.getRecentCount()
        assertThat(count).isEqualTo(doctors.filter { it.isRecent }.size)
    }

    @Test
    @Throws(Exception::class)
    fun testDatabaseReturnsAllRecentDoctors() {
        // Given
        val doctors = listOfDoctorViewItem

        // When
        doctorsDao.insertAll(doctors)

        // Then
        val recentDoctors = doctorsDao.getAllRecentDoctors()
        assertThat(recentDoctors.size).isEqualTo(recentDoctors.filter { it.isRecent }.size)
    }

    @Test
    @Throws(Exception::class)
    @ExperimentalCoroutinesApi
    fun testDatabaseObserveRecentDoctorList() = coroutinesTestRule.runBlockingTest {
        // Given
        val doctors = listOfDoctorViewItem

        // When
        doctorsDao.insertAll(doctors)

        lifeCycleTestOwner.onResume()

        // Then
        val recentDoctors = doctorsDao.getRecentDoctorList("%%")
        recentDoctors.observe(lifeCycleTestOwner, observer)
        assertThat(recentDoctors.value).hasSize(doctors.filter { it.isRecent }.size)
        assertThat(recentDoctors.value).isEqualTo(doctors.filter { it.isRecent })
    }

    @Test
    @Throws(Exception::class)
    fun testDatabaseReturnsDoctorsById() {
        // Given
        val doctors = listOfDoctorViewItem
        val id = "ChIJyZz_b-lRqEcRI7WMafDasLg"

        // When
        doctorsDao.insertAll(doctors)

        // Then
        val byId = doctorsDao.getDoctorsById(id)
        assertThat(byId).isNotNull()
        assertThat(byId.id).isEqualTo(id)
        assertThat(byId).isEqualTo(doctors.first())
    }

    @Test
    @Throws(Exception::class)
    @ExperimentalCoroutinesApi
    fun testDatabaseUpdateVisitingTimeById() = coroutinesTestRule.runBlockingTest {
        // Given
        val doctors = listOfDoctorViewItem
        val id = "ChIJDWzpck5OqEcRB82iUBz12G0"
        val now = Date(System.currentTimeMillis())

        // When
        doctorsDao.insertAll(doctors)

        // Then
        doctorsDao.updateVisitingTimeById(now, id)
        val doctor = doctorsDao.getDoctorsById(id)
        val recentCount = doctorsDao.getRecentCount()
        assertThat(doctor.isRecent).isTrue()
        // We know it has been increased by 1
        assertThat(recentCount).isEqualTo(doctors.filter { it.isRecent }.size + 1)
    }

    @Test
    @Throws(Exception::class)
    @ExperimentalCoroutinesApi
    fun testDatabaseRemoveFromRecent() = coroutinesTestRule.runBlockingTest {
        // Given
        val doctors = listOfDoctorViewItem
        val id = "ChIJXQ6CX-RRqEcRr2Tg5peM9d2"

        // When
        doctorsDao.insertAll(doctors)

        // Then
        doctorsDao.removeFromRecent(id)
        val recentCount = doctorsDao.getRecentCount()
        // We know it has been decreased by 1
        assertThat(recentCount).isEqualTo(doctors.filter { it.isRecent }.size - 1)
    }

    @Throws(IOException::class)
    override fun tearDown() {
        super.tearDown()
        db.close()
        lifeCycleTestOwner.onDestroy()
    }
}
