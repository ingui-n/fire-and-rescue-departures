package com.android.fire_and_rescue_departures.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.fire_and_rescue_departures.consts.UIText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesSearchItem(
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    scope: CoroutineScope,
) {
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text(UIText.SEARCH_PLACEHOLDER.value) },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = UIText.BACK.value
                            )
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            )
        }

    TopSearchBar(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
        modifier = modifier.padding(top = 0.dp),
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
    ) {
        SearchResults(
            onResultClick = { result ->
                textFieldState.setTextAndPlaceCursorAtEnd(result)
                scope.launch { searchBarState.animateToCollapsed() }
            }
        )
    }
}

@Composable
fun SearchResults(
    onResultClick: (String) -> Unit
) {
    val results = listOf("Station A", "Station B", "Station C")//todo handle filtering
    LazyColumn {
        items(count = results.size) { index ->
            val result = results[index]
            Text(
                text = result,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onResultClick(result) }
                    .padding(16.dp)
            )
        }
    }
}