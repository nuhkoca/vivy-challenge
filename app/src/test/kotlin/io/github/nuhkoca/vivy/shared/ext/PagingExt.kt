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
package io.github.nuhkoca.vivy.shared.ext

import android.database.Cursor
import androidx.lifecycle.Observer
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.paging.LimitOffsetDataSource
import com.google.common.truth.Truth.assertThat
import io.github.nuhkoca.vivy.LifeCycleTestOwner
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.mockk.every
import io.mockk.mockk

fun <T> List<T>.asPagedList() = LivePagedListBuilder(
    createMockDataSourceFactory(this),
    Config(
        enablePlaceholders = false,
        prefetchDistance = 24,
        pageSize = if (size == 0) 1 else size
    )
).build()

private fun <T> createMockDataSourceFactory(itemList: List<T>): DataSource.Factory<Int, T> =
    object : DataSource.Factory<Int, T>() {
        override fun create(): DataSource<Int, T> = MockLimitDataSource(itemList)
    }

private val mockQuery = mockk<RoomSQLiteQuery>(relaxed = true) {
    every { sql } returns ""
}

private val mockDb = mockk<RoomDatabase>(relaxed = true) {
    every { invalidationTracker } returns mockk(relaxUnitFun = true)
}

private class MockLimitDataSource<T>(private val itemList: List<T>) :
    LimitOffsetDataSource<T>(mockDb, mockQuery, false, null) {
    override fun convertRows(cursor: Cursor?): MutableList<T> = itemList.toMutableList()
    override fun countItems(): Int = itemList.count()
    override fun isInvalid(): Boolean = false
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        /* Not implemented */
    }

    override fun loadRange(startPosition: Int, loadCount: Int) =
        itemList.subList(startPosition, startPosition + loadCount).toMutableList()

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        callback.onResult(itemList, 0)
    }
}

fun <T> PagedList<T>.loadAllData() {
    do {
        val oldSize = this.loadedCount
        this.loadAround(this.size - 1)
    } while (this.size != oldSize)
}

fun getPagedList(
    listing: List<DoctorViewItem>,
    lifeCycleTestOwner: LifeCycleTestOwner
): PagedList<DoctorViewItem> {
    val observer = LoggingObserver<PagedList<DoctorViewItem>>()
    val paged = listing.asPagedList()
    paged.observe(lifeCycleTestOwner, observer)
    assertThat(observer.value).isNotNull()
    return observer.value!!
}

/**
 * Simple observer that logs the latest value it receives
 */
private class LoggingObserver<T> : Observer<T> {
    var value: T? = null
    override fun onChanged(t: T?) {
        this.value = t
    }
}
