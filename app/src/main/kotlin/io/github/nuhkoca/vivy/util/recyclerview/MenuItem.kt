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
package io.github.nuhkoca.vivy.util.recyclerview

import androidx.recyclerview.widget.RecyclerView

/**
 * A data class that represents context menu for [RecyclerView] items.
 *
 * @property id The menu id
 * @property item The item
 */
data class MenuItem(val id: Int, val item: Any?) {
    companion object {
        const val ITEM_ID_MAP = 101
        const val ITEM_ID_CALL = 102
        const val ITEM_ID_EMAIL = 103
        const val ITEM_ID_WEBSITE = 104
    }
}
