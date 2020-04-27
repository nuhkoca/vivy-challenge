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
package io.github.nuhkoca.vivy

import io.github.nuhkoca.vivy.data.model.Location
import io.github.nuhkoca.vivy.data.model.inline.Latitude
import io.github.nuhkoca.vivy.data.model.inline.Longitude
import io.github.nuhkoca.vivy.data.model.raw.Doctor
import io.github.nuhkoca.vivy.data.model.raw.Doctors
import io.github.nuhkoca.vivy.data.model.view.DoctorViewItem
import java.util.*

object Stubs {

    private val now = Date(System.currentTimeMillis())

    val listOfDoctorViewItem = listOf(
        DoctorViewItem(
            id = "ChIJyZz_b-lRqEcRI7WMafDasLg",
            name = "Dr. med. Mario Voss",
            photoId = null,
            rating = 2.5,
            address = "Friedrichstraße 115, 10117 Berlin, Germany",
            location = Location(Latitude(52.526779), Longitude(13.387201)),
            phoneNumber = "+29 30 28391555",
            email = "null",
            website = "http://www.vivy-doc.de/",
            recentVisiting = now,
            isRecent = false
        ),
        DoctorViewItem(
            id = "ChIJH6T7dVJOqEcRLkHjhRXX_22",
            name = "Daniel Engert and Annette Cotanidis",
            photoId = null,
            rating = 2.3,
            address = "Falckensteinstraße 8, 10997 Berlin, Germany",
            location = Location(Latitude(52.2990923), Longitude(13.2225777)),
            phoneNumber = "+29 30 6125508",
            email = "null",
            website = "http://www.vivy-doc.de/",
            recentVisiting = now,
            isRecent = true
        ),
        DoctorViewItem(
            id = "ChIJf8-bEOlRqEcRQ_YgOlo580k",
            name = "Gemeinschaftspraxis Dr. Hintsche und Dr. Klausen",
            photoId = null,
            rating = 5.0,
            address = "Linienstraße 127, 10115 Berlin, Germany",
            location = Location(Latitude(52.5266526), Longitude(13.3895222)),
            phoneNumber = "+29 30 2825052",
            email = "null",
            website = "http://www.vivy-doc.de/",
            recentVisiting = now,
            isRecent = true
        ),
        DoctorViewItem(
            id = "ChIJDWzpck5OqEcRB82iUBz12G0",
            name = "Gemeinschaftspraxis Schlesisches Tor",
            photoId = "https://vivy.com/interviews/challenges/android/img/people/1.jpeg",
            rating = 2.2,
            address = "Köpenicker Str. 1, 10997 Berlin, Germany",
            location = Location(Latitude(52.5015222), Longitude(13.2215282)),
            phoneNumber = "+29 30 6123233",
            email = "null",
            website = "http://www.vivy-doc.de/",
            recentVisiting = now,
            isRecent = false
        ),
        DoctorViewItem(
            id = "ChIJXQ6CX-RRqEcRr2Tg5peM9d2",
            name = "Jasmin Malak",
            photoId = "https://vivy.com/interviews/challenges/android/img/people/2.jpeg",
            rating = 3.3,
            address = "Weinbergsweg 1, 10119 Berlin, Germany",
            location = Location(Latitude(52.5300568), Longitude(13.2018832)),
            phoneNumber = "+29 30 220307710",
            email = "null",
            website = "http://www.vivy-doc.de/",
            recentVisiting = now,
            isRecent = true
        )
    )

    val selectedDoctorViewItem = DoctorViewItem(
        id = "ChIJW8HE26RPqEcRajIplsHXa8E",
        name = "Dr. med. Garney Micus",
        photoId = "https://vivy.com/interviews/challenges/android/img/people/13.jpeg",
        rating = 3.8,
        address = "Donaustraße 105, 12023 Berlin, Germany",
        location = Location(Latitude(52.2821683), Longitude(13.2363021)),
        phoneNumber = "+29 30 6873993",
        email = "",
        website = "http://www.vivy-doc.de/",
        isRecent = true
    )

    val listOfDoctors = Doctors(
        listOf(
            Doctor(
                id = "ChIJyZz_b-lRqEcRI7WMafDasLg",
                name = "Dr. med. Mario Voss",
                photoId = null,
                rating = 2.5,
                address = "Friedrichstraße 115, 10117 Berlin, Germany",
                latitude = 52.526779,
                longitude = 13.387201,
                highlighted = false,
                reviewCount = 3,
                specialityIds = listOf(),
                source = "google",
                phoneNumber = "+29 30 28391555",
                email = "null",
                website = "http://www.vivy-doc.de/",
                openingHours = listOf(),
                integration = null,
                translation = null
            ),
            Doctor(
                id = "ChIJH6T7dVJOqEcRLkHjhRXX_22",
                name = "Daniel Engert and Annette Cotanidis",
                photoId = null,
                rating = 2.3,
                address = "Falckensteinstraße 8, 10997 Berlin, Germany",
                latitude = 52.2990923,
                longitude = 13.2225777,
                highlighted = false,
                reviewCount = 3,
                specialityIds = listOf(),
                source = "google",
                phoneNumber = "+29 30 6125508",
                email = "null",
                website = "http://www.vivy-doc.de/",
                openingHours = listOf(),
                integration = null,
                translation = null
            ),
            Doctor(
                id = "ChIJf8-bEOlRqEcRQ_YgOlo580k",
                name = "Gemeinschaftspraxis Dr. Hintsche und Dr. Klausen",
                photoId = null,
                rating = 5.0,
                address = "Linienstraße 127, 10115 Berlin, Germany",
                latitude = 52.5266526,
                longitude = 13.3895222,
                highlighted = false,
                reviewCount = 3,
                specialityIds = listOf(),
                source = "google",
                phoneNumber = "+29 30 2825052",
                email = "null",
                website = "http://www.vivy-doc.de/",
                openingHours = listOf(),
                integration = null,
                translation = null
            ),
            Doctor(
                id = "ChIJDWzpck5OqEcRB82iUBz12G0",
                name = "Gemeinschaftspraxis Schlesisches Tor",
                photoId = "https://vivy.com/interviews/challenges/android/img/people/1.jpeg",
                rating = 2.2,
                address = "Köpenicker Str. 1, 10997 Berlin, Germany",
                latitude = 52.5015222,
                longitude = 13.2215282,
                highlighted = false,
                reviewCount = 3,
                specialityIds = listOf(),
                source = "google",
                phoneNumber = "+29 30 6123233",
                email = "null",
                website = "http://www.vivy-doc.de/",
                openingHours = listOf(),
                integration = null,
                translation = null
            ),
            Doctor(
                id = "ChIJXQ6CX-RRqEcRr2Tg5peM9d2",
                name = "Jasmin Malak",
                photoId = "https://vivy.com/interviews/challenges/android/img/people/2.jpeg",
                rating = 3.3,
                address = "Weinbergsweg 1, 10119 Berlin, Germany",
                latitude = 52.5300568,
                longitude = 13.2018832,
                highlighted = false,
                reviewCount = 3,
                specialityIds = listOf(),
                source = "google",
                phoneNumber = "+29 30 220307710",
                email = "null",
                website = "http://www.vivy-doc.de/",
                openingHours = listOf(),
                integration = null,
                translation = null
            )
        ), "CvQD7gEAAKjcb"
    )
}
