package com.android.fire_and_rescue_departures

import com.android.fire_and_rescue_departures.data.DeparturesMapEntity
import com.android.fire_and_rescue_departures.data.MyObjectBox
import com.android.fire_and_rescue_departures.viewmodels.DeparturesMapViewModel
import io.objectbox.BoxStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val viewModelModule = module {
    viewModel { DeparturesMapViewModel(/*get()*/) }
}

val networkModule = module {
    single { provideOkHttpClient() }
}

val objectBoxModule = module {
    single {
        MyObjectBox.builder()
            .androidContext(androidContext())
            .build()
    }
    single { get<BoxStore>().boxFor(DeparturesMapEntity::class.java) }
}

fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}
