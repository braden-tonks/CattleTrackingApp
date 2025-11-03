package com.example.compose.snippets.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp

enum class SearchBehavior { Dropdown, Inline }

/**
 * A reusable, customizable search bar composable that supports live search and result selection.
 *
 * This component handles user input, triggers a search callback when text changes or the search
 * action is performed, and displays matching results in a dropdown or expanded list format.
 *
 * It is **generic** (`<T>`), meaning you can use it for any data type (e.g., AnimalUi, Product, User),
 * as long as you provide how to display each result via composable lambdas.
 *
 * ---
 * ## Parameters
 *
 * @param query The current search text input value.
 * @param onQueryChange A callback triggered whenever the search text changes.
 *                      Typically used to update the ViewModel and perform a new search.
 * @param onSearch A callback triggered when the user presses the search/enter key.
 *                 Use this for manual search execution or logging.
 * @param searchResults A list of items matching the current search query.
 *                      This list is displayed under the search bar when available.
 * @param onResultClick A callback triggered when the user selects an item from the search results.
 *                      Typically used to navigate to a detail page.
 * @param modifier Modifier for styling and layout customization (optional).
 * @param searchBehavior Controls how the search results are displayed (e.g., dropdown or expanded list).
 *                       Defaults to [SearchBehavior.Dropdown].
 * @param minCharsToSearch Minimum number of characters required before triggering search results.
 *                         Defaults to 1.
 * @param headlineContent A composable used to define the main text or title for each search result.
 *                        Example: `{ item -> Text(item.tagNumber) }`
 * @param supportingContent A composable used to define additional supporting text or details for
 *                          each result. Example: `{ item -> Text("Type: ${item.type.displayName}") }`
 *
 * ---
 * ## Notes
 * - Make sure your ViewModel updates `searchResults` reactively when `onQueryChange` is called.
 * - The generic type `<T>` allows this search bar to be used across multiple data models.
 * - Customize the `headlineContent` and `supportingContent` composables to control how results appear.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomizableSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<T>,
    onResultClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    searchBehavior: SearchBehavior = SearchBehavior.Dropdown,
    minCharsToSearch: Int = 1,
    headlineContent: (@Composable (T) -> Unit)? = null,
    supportingContent: (@Composable (T) -> Unit)? = null
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Only show dropdown when results exist
    val shouldExpand = expanded && searchBehavior == SearchBehavior.Dropdown
    val focusManager = LocalFocusManager.current

    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .then(
                    if (shouldExpand || searchBehavior == SearchBehavior.Inline)
                        Modifier.fillMaxWidth() // no padding when expanded
                    else
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                )
                .semantics { traversalIndex = 0f },
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        // Run the provided onSearch (e.g., trigger VM query)
                        onSearch(query)
                        // do NOT collapse here — keep expanded so results can be displayed
                        // Optionally clear focus to close the keyboard but keep the dropdown:
                        focusManager.clearFocus(force = true)
                    },
                    expanded = shouldExpand,
                    onExpandedChange = { isActive ->
                        // Only update expanded state if not inline
                        if (searchBehavior != SearchBehavior.Inline) {
                            expanded = isActive
                        }
                    },
                    placeholder = { Text("Search Tag Number") },
                    leadingIcon = {
                        if (shouldExpand) {
                            IconButton(onClick = { expanded = false }) {
                                Icon(
                                    Icons.Default.ArrowBackIosNew,
                                    contentDescription = "Back Arrow"
                                )
                            }
                        } else Icon(Icons.Default.Search, contentDescription = "Search")
                    },

                    )
            },
            expanded = shouldExpand,
            onExpandedChange = { isActive ->
                if (searchBehavior != SearchBehavior.Inline) expanded = isActive
            },
        ) {
            // Show dropdown results only when we have items
            if (shouldExpand && searchBehavior == SearchBehavior.Dropdown) {
                LazyColumn {
                    items(searchResults) { result ->
                        ListItem(
                            headlineContent = {
                                headlineContent?.invoke(result) ?: Text(result.toString())
                            },
                            supportingContent = {
                                supportingContent?.invoke(result) ?: Text(result.toString())
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .clickable {
                                    onResultClick(result)
                                    expanded = false
                                }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


