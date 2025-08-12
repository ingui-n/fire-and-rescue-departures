package com.android.fire_and_rescue_departures.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureEntity
import com.android.fire_and_rescue_departures.data.DepartureEntity_
import io.objectbox.Box
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class DeparturesBookmarksViewModel(
    private val departuresBox: Box<DepartureEntity>
) : ViewModel() {
    private val _departureBookmarks =
        MutableStateFlow<ApiResult<List<DepartureEntity>>>(ApiResult.Loading)
    val departureBookmarks: StateFlow<ApiResult<List<DepartureEntity>>> =
        _departureBookmarks.asStateFlow()

    init {
        loadDepartureBookmarks()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDepartureBookmark(departure: Departure) {
        viewModelScope.launch {
            val bookmark = departuresBox.query()
                .equal(DepartureEntity_.departureId, departure.id)
                .build()
                .findFirst()

            if (bookmark != null) {
                bookmark.isBookmarked = true
                departuresBox.put(bookmark)
            }
            loadDepartureBookmarks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeDepartureBookmark(departureId: Long) {
        viewModelScope.launch {
            val bookmark = departuresBox.query()
                .equal(DepartureEntity_.departureId, departureId)
                .build()
                .findFirst()

            if (bookmark != null) {
                bookmark.isBookmarked = false
                departuresBox.put(bookmark)
            }
            loadDepartureBookmarks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDepartureBookmarks() {
        val bookmarks = departuresBox.query()
            .equal(DepartureEntity_.isBookmarked, true)
            .build()
            .find()

        bookmarks.reverse()
        _departureBookmarks.value = ApiResult.Success(bookmarks)
    }
}
