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
package io.github.nuhkoca.vivy.data.service

import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.verifier.VivyHostnameVerifier
import io.github.nuhkoca.vivy.shared.MEDIA_TYPE_DEFAULT
import io.github.nuhkoca.vivy.shared.dispatcher.ErrorDispatcher
import io.github.nuhkoca.vivy.shared.dispatcher.SuccessDispatcher
import io.github.nuhkoca.vivy.shared.dispatcher.TimeoutDispatcher
import io.github.nuhkoca.vivy.util.ext.manipulateUrl
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection.HTTP_NOT_FOUND

/**
 * A test class for [DoctorsService]
 */
@RunWith(MockitoJUnitRunner::class)
@LargeTest
class DoctorsServiceTest {

    /*
     -----------------------
    |    Private members    |
     -----------------------
    */
    private val mockWebServer = MockWebServer()
    private lateinit var doctorsService: DoctorsService

    @Before
    @UnstableDefault
    fun setUp() {
        mockWebServer.start()

        val client = OkHttpClient.Builder().apply {
            hostnameVerifier(VivyHostnameVerifier)
            connectTimeout(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
            readTimeout(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
            writeTimeout(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
            addInterceptor(HttpLoggingInterceptor())
        }.build()

        val retrofit = Retrofit.Builder().apply {
            client(client)
            baseUrl(mockWebServer.url("/").toString())
            addConverterFactory(Json.asConverterFactory(MEDIA_TYPE_DEFAULT.toMediaType()))
        }.build()

        doctorsService = retrofit.create()

        mockWebServer.dispatcher = SuccessDispatcher(DOCTORS_SUCCESS_RESPONSE_FILE_NAME)
    }

    /*
     * We are unable to manipulate Retrofit's dispatcher so that we have to use runBlocking
     */
    @Test
    @Suppress("BlockingMethodInNonBlockingContext")
    fun `list of doctors should be fetched`() = runBlocking {
        // Given
        val lastKey = null
        val url = manipulateUrl(lastKey)

        // When
        val response = doctorsService.getDoctorList(url).execute()

        // Then
        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).isNotNull()
        assertThat(response.body()?.lastKey).isNotNull()
        assertThat(response.body()?.doctors).isNotNull()
        assertThat(response.body()?.doctors).hasSize(20)
    }

    /*
     * We are unable to manipulate Retrofit's dispatcher so that we have to use runBlocking
     */
    @Test
    @Suppress("BlockingMethodInNonBlockingContext")
    fun `doctors service should throw an error`() = runBlocking {
        mockWebServer.dispatcher = ErrorDispatcher

        // Given
        val lastKey = null
        val url = manipulateUrl(lastKey)

        // When
        val response = doctorsService.getDoctorList(url).execute()

        assertThat(response.isSuccessful).isFalse()
        assertThat(response.message()).isNotNull()
        assertThat(response.message()).isEqualTo("Client Error")
        assertThat(response.code()).isEqualTo(HTTP_NOT_FOUND)
    }

    /*
     * We are unable to manipulate Retrofit's dispatcher so that we have to use runBlocking
     */
    @Test
    @Suppress("BlockingMethodInNonBlockingContext")
    fun `request should be timed out`() = runBlocking {
        mockWebServer.dispatcher = TimeoutDispatcher

        // Given
        val lastKey = null
        val url = manipulateUrl(lastKey)
        var response: Response<Doctors>? = null

        // When
        try {
            response = doctorsService.getDoctorList(url).execute()
        } catch (e: SocketTimeoutException) {
            println(e)
            assertThat(e.message).isNotNull()
            assertThat(e.message).isIn(listOf("timeout", "Read timed out"))
        }

        // Then
        assertThat(response).isNull()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private companion object {
        // For test purpose <Real one is 10sec>
        private const val TIMEOUT_IN_MS = 1000L
        private const val DOCTORS_SUCCESS_RESPONSE_FILE_NAME = "doctors_success_response.json"
    }
}
