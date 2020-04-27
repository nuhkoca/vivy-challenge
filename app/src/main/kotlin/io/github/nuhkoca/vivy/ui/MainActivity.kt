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
package io.github.nuhkoca.vivy.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import io.github.nuhkoca.vivy.R
import io.github.nuhkoca.vivy.databinding.ActivityMainBinding
import io.github.nuhkoca.vivy.util.ext.startLicenseActivity
import io.github.nuhkoca.vivy.util.ext.viewBinding
import io.github.nuhkoca.vivy.util.searchview.DefaultQueryTextListener

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val navHostFragment by lazy {
        // Extension is not working, bug link: https://issuetracker.google.com/issues/142847973
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val defaultQueryTextListener = object : DefaultQueryTextListener() {
        override fun onQueryTextChange(newText: String): Boolean {
            val fragments =
                navHostFragment.childFragmentManager.fragments.filterIsInstance<Searchable>()
            fragments.forEach { searchable -> searchable.onQueryChange(newText) }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        appBarConfiguration = AppBarConfiguration.Builder(R.id.doctorsFragment).build()
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = menu.findItem(R.id.menu_search_item)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (searchView?.actionView as? SearchView)?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(defaultQueryTextListener)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_library_item -> {
                startLicenseActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
