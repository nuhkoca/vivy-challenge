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
package io.github.nuhkoca.vivy.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.github.nuhkoca.vivy.BuildConfig
import io.github.nuhkoca.vivy.BuildConfig.BASE_URL
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorsViewItem
import io.github.nuhkoca.vivy.data.service.DoctorsService
import io.github.nuhkoca.vivy.data.verifier.VivyHostnameVerifier
import io.github.nuhkoca.vivy.db.di.DatabaseModule
import io.github.nuhkoca.vivy.domain.mapper.DoctorsViewItemMapper
import io.github.nuhkoca.vivy.domain.repository.DoctorsRepository
import io.github.nuhkoca.vivy.domain.repository.Repository
import io.github.nuhkoca.vivy.util.coroutines.DefaultDispatcherProvider
import io.github.nuhkoca.vivy.util.coroutines.DispatcherProvider
import io.github.nuhkoca.vivy.util.mapper.Mapper
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class])
abstract class AppModule {

    @Binds
    @Singleton
    internal abstract fun bindDoctorsRepository(
        doctorsRepository: DoctorsRepository
    ): Repository

    @Binds
    @Singleton
    internal abstract fun bindDoctorsViewItemMapper(
        doctorsViewItemMapper: DoctorsViewItemMapper
    ): Mapper<Doctors, DoctorsViewItem>

    @Binds
    @Singleton
    internal abstract fun bindDispatcherProvider(
        defaultDispatcherProvider: DefaultDispatcherProvider
    ): DispatcherProvider

    @Module
    internal companion object {

        private const val MEDIA_TYPE_DEFAULT = "application/json"
        private const val TIMEOUT_IN_MS = 10000L

        @Provides
        @Singleton
        internal fun provideCurrencyService(retrofit: Retrofit): DoctorsService = retrofit.create()

        @Provides
        @Singleton
        @UnstableDefault
        internal fun provideRetrofit(@InternalApi httpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder().apply {
                baseUrl(BASE_URL)
                addConverterFactory(Json.asConverterFactory(MEDIA_TYPE_DEFAULT.toMediaType()))
                client(httpClient)
            }.build()
        }

        @Provides
        @Singleton
        @InternalApi
        @UnstableDefault
        internal fun provideOkHttpClient(
            @InternalApi loggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder().apply {
                hostnameVerifier(VivyHostnameVerifier)
                connectTimeout(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                readTimeout(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                writeTimeout(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                addInterceptor(loggingInterceptor)
            }.build()
        }

        @Provides
        @Singleton
        @InternalApi
        internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            }
        }

        @Provides
        @Singleton
        internal fun provideIOExecutor(): Executor = Executors.newSingleThreadExecutor()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
private annotation class InternalApi
