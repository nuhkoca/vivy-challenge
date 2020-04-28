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
package io.github.nuhkoca.vivy.db.dao

import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.db.VivyDB
import java.util.*

/**
 * The Data Access Object for [VivyDB] to allow transactions for [DoctorViewItem]
 */
@Dao
interface DoctorsDao {

    @Query("SELECT * FROM doctors WHERE is_recent = 0 ORDER BY rating DESC")
    fun getDoctorList(): DataSource.Factory<Int, DoctorViewItem>

    // We need this separate query since [getDoctorList] is in relation with Boundary Callback and
    // search query is not proper, application consistently tries to hit service as it understands
    // database is empty
    // No need to filter by is_recent because I want to see that doctor in case of filter, otherwise
    // it won't be visible
    @Query("SELECT * FROM doctors WHERE LOWER(name) LIKE :name ORDER BY rating DESC")
    fun getDoctorsByName(name: String): DataSource.Factory<Int, DoctorViewItem>

    @VisibleForTesting
    @Query("SELECT * FROM doctors WHERE id =:id")
    fun getDoctorsById(id: String): DoctorViewItem

    @Query("SELECT * FROM doctors WHERE LOWER(name) LIKE :name AND is_recent = 1 ORDER BY recent_visiting DESC LIMIT 3")
    fun getRecentDoctorList(name: String): LiveData<List<DoctorViewItem>>

    @Query("SELECT * FROM doctors WHERE is_recent = 1 ORDER BY recent_visiting DESC")
    fun getAllRecentDoctors(): List<DoctorViewItem>

    @Query("SELECT COUNT(id) FROM doctors WHERE is_recent = 1")
    fun getRecentCount(): Int

    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(doctorViewItem: List<DoctorViewItem>)

    @WorkerThread
    @Query("UPDATE doctors SET recent_visiting=:time,is_recent = 1 WHERE id = :id")
    suspend fun updateVisitingTimeById(time: Date, id: String)

    @WorkerThread
    @Query("UPDATE doctors SET is_recent = 0 WHERE id = :id")
    suspend fun removeFromRecent(id: String)
}
