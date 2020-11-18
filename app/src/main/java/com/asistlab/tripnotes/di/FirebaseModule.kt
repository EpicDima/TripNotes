package com.asistlab.tripnotes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @author EpicDima
 */
@Module
@InstallIn(ApplicationComponent::class)
object FirebaseModule {
    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideDatabase(): DatabaseReference = Firebase.database.reference

    @Singleton
    @Provides
    fun provideStorage(): StorageReference = Firebase.storage.reference
}