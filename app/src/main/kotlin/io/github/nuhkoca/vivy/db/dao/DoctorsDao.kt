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

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem

@Dao
interface DoctorsDao {

    @Query("SELECT * FROM doctors ORDER BY rating DESC")
    fun getDoctorList(): DataSource.Factory<Int, DoctorViewItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(doctorViewItem: List<DoctorViewItem>)
}
