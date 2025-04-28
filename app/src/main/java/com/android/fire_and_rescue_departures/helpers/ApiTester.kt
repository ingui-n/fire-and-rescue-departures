package com.android.fire_and_rescue_departures.helpers

import androidx.lifecycle.ViewModel
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.consts.regions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class ApiTester(private val departuresApi: DeparturesApi): ViewModel() {
    fun test() {
        regions.forEach { region ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeout(2000L) {
                        departuresApi.test("${region.url}/api")
                    }
                } catch (_: Exception) {
                    region.available = false
                }
            }
        }
    }
}
