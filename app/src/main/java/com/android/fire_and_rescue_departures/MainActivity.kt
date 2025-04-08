package com.android.fire_and_rescue_departures

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.android.fire_and_rescue_departures.ui.CenterAlignedTopAppBarExample
import com.android.fire_and_rescue_departures.ui.theme.BaseAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(

            )
        }
        setContent {
            BaseAppTheme {
                CenterAlignedTopAppBarExample()
            }
        }
    }
}
