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
package io.github.nuhkoca.vivy.ui.doctors.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.ui.di.MainScope
import javax.inject.Inject
import javax.inject.Provider

/**
 * A [DataSource.Factory] that produce a data source to fetch list of doctors.
 *
 * @param dataSource The [PageKeyedDataSource] for list of doctors
 */
@MainScope
class DoctorsDataSourceFactory @Inject constructor(
    private val dataSource: Provider<PageKeyedDataSource<String, DoctorViewItem>>
) : DataSource.Factory<String, DoctorViewItem>() {

    val mutableDataSource = MutableLiveData<DoctorsDataSource>()

    override fun create(): DataSource<String, DoctorViewItem> {
        // This is for sake of invalidate of data source for retry operation
        val dataSource = dataSource.get()
        mutableDataSource.postValue(dataSource as DoctorsDataSource)
        return dataSource
    }
}
