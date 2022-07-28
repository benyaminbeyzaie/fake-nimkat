package com.nimkat.app.di

import android.content.Context
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.utils.AuthPrefs
import com.nimkat.app.utils.FirebaseServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://nimkat.app/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideNimkatApi(retrofit: Retrofit): NimkatApi = retrofit.create(NimkatApi::class.java)

    @Provides
    @Singleton
    fun provideAuthPrefs(@ApplicationContext context: Context): AuthPrefs = AuthPrefs(context)

    @Provides
    @Singleton
    fun provideFirebaseServices(): FirebaseServices = FirebaseServices()
}