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
package io.github.nuhkoca.vivy.db.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.nuhkoca.vivy.db.VivyDB
import javax.inject.Singleton

@Module
internal class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideVivyDatabase(context: Context): VivyDB {
        return Room.databaseBuilder(context, VivyDB::class.java, "vivy.db").build()
    }
}
