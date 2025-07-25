package com.android.fire_and_rescue_departures

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.data.DepartureBookmarkEntity
import com.android.fire_and_rescue_departures.data.MyObjectBox
import com.android.fire_and_rescue_departures.repository.DepartureBookmarksRepository
import com.android.fire_and_rescue_departures.viewmodels.DeparturesBookmarksViewModel
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import com.android.fire_and_rescue_departures.viewmodels.DeparturesMapViewModel
import io.objectbox.BoxStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val repositoryModule = module {
    single { DepartureBookmarksRepository(get()) }
}

@RequiresApi(Build.VERSION_CODES.O)
val viewModelModule = module {
    viewModel { DeparturesListViewModel(get(), get(), get(), androidContext()) }
    viewModel { DeparturesBookmarksViewModel(get(), get()) }
    viewModel { DeparturesMapViewModel() }
}

val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit() }
    single { provideDeparturesApi(get()) }
}

val imageModule = module {
    single { provideImageLoader(androidContext()) }
}

val objectBoxModule = module {
    single {
        MyObjectBox.builder()
            .androidContext(androidContext())
            .build()
    }
    single { get<BoxStore>().boxFor(DepartureBookmarkEntity::class.java) }
}

fun provideOkHttpClient(): OkHttpClient {
    val trustAllCerts = arrayOf<TrustManager>(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    val sslContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, SecureRandom())
    }

    val sslSocketFactory = sslContext.socketFactory

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    return OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .addInterceptor(loggingInterceptor)
        .build()


    /*val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()*/
}

fun getUnsafeOkHttpClient(): OkHttpClient {
    try {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

fun provideRetrofit(): Retrofit {
    val okHttpClient = getUnsafeOkHttpClient()

    return Retrofit.Builder()
        .baseUrl("https://no-base-url-used.do-not-remove.nonexistent")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideDeparturesApi(retrofit: Retrofit): DeparturesApi {
    return retrofit.create(DeparturesApi::class.java)
}

fun provideImageLoader(androidContext: Context): ImageLoader {
    return ImageLoader.Builder(androidContext)
        .memoryCache {
            MemoryCache.Builder(androidContext)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(androidContext.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()
}
