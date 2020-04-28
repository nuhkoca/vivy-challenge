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
package io.github.nuhkoca.vivy.ui.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import io.github.nuhkoca.vivy.di.factory.FragmentKey
import io.github.nuhkoca.vivy.di.factory.ViewModelKey
import io.github.nuhkoca.vivy.ui.detail.DoctorDetailFragment
import io.github.nuhkoca.vivy.ui.doctors.DoctorsFragment
import io.github.nuhkoca.vivy.ui.doctors.DoctorsViewModel
import io.github.nuhkoca.vivy.ui.doctors.adapter.DoctorsAdapter
import io.github.nuhkoca.vivy.ui.doctors.adapter.DoctorsLoadStateAdapter
import io.github.nuhkoca.vivy.util.recyclerview.MenuItem
import io.github.nuhkoca.vivy.ui.doctors.adapter.RecentDoctorsAdapter
import io.github.nuhkoca.vivy.util.event.SingleLiveEvent
import javax.inject.Scope

@Module
internal abstract class MainModule {

    @Binds
    @IntoMap
    @MainScope
    @FragmentKey(DoctorsFragment::class)
    internal abstract fun bindDoctorsFragment(doctorsFragment: DoctorsFragment): Fragment

    @Binds
    @IntoMap
    @MainScope
    @FragmentKey(DoctorDetailFragment::class)
    internal abstract fun bindDoctorDetailFragment(doctorDetailFragment: DoctorDetailFragment): Fragment

    @Binds
    @IntoMap
    @MainScope
    @ViewModelKey(DoctorsViewModel::class)
    internal abstract fun bindDoctorsViewModel(viewModel: DoctorsViewModel): ViewModel

    @Module
    internal companion object {

        @Provides
        @MainScope
        internal fun provideDoctorsAdapter(
            itemClickLiveData: SingleLiveEvent<DoctorViewItem>,
            menuClickLiveData: SingleLiveEvent<MenuItem>
        ) = DoctorsAdapter(itemClickLiveData, menuClickLiveData)

        @Provides
        @MainScope
        internal fun provideRecentDoctorsAdapter(
            itemClickLiveData: SingleLiveEvent<DoctorViewItem>,
            menuClickLiveData: SingleLiveEvent<MenuItem>
        ) = RecentDoctorsAdapter(itemClickLiveData, menuClickLiveData)

        @Provides
        @MainScope
        internal fun provideDoctorsLoadStateAdapter(
            retryClickListener: SingleLiveEvent<Unit>
        ) = DoctorsLoadStateAdapter(retryClickListener)

        @Provides
        @MainScope
        internal fun provideItemClickLiveData() = SingleLiveEvent<DoctorViewItem>()

        @Provides
        @MainScope
        internal fun provideRetryClickListener() = SingleLiveEvent<Unit>()

        @Provides
        @MainScope
        internal fun provideMenuClickListener() = SingleLiveEvent<MenuItem>()
    }
}

@Scope
@MustBeDocumented
internal annotation class MainScope
