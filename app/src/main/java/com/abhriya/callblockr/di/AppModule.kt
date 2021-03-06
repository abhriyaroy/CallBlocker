package com.abhriya.callblockr.di

import android.app.Application
import android.content.Context
import com.abhriya.callblockr.NotificationProvider
import com.abhriya.callblockr.NotificationsProviderImpl
import com.abhriya.callblockr.PhoneReceiver
import com.abhriya.callblockr.PhoneReceiverImpl
import com.abhriya.callblockr.data.repository.ContactsRepository
import com.abhriya.callblockr.data.repository.ContactsRepositoryImpl
import com.abhriya.callblockr.data.source.CallLogDataSource
import com.abhriya.callblockr.data.source.CallLogDataSourceImpl
import com.abhriya.callblockr.data.source.ContactsDataSource
import com.abhriya.callblockr.data.source.ContactsDataSourceImpl
import com.abhriya.commons.SystemPermissionUtil
import com.abhriya.datasource.local.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun providesContactRepository(
        localDataSource: LocalDataSource,
        contactsDataSource: ContactsDataSource,
        callLogDataSource: CallLogDataSource
    ): ContactsRepository =
        ContactsRepositoryImpl(
            localDataSource,
            contactsDataSource,
            callLogDataSource
        )

    @Provides
    @Singleton
    fun providesPhoneReceiver(
        localDataSource: LocalDataSource,
        notificationProvider: NotificationProvider
    ): PhoneReceiver = PhoneReceiverImpl(localDataSource, notificationProvider)

    @Provides
    @Singleton
    fun providesNotificationsProvider(systemPermissionUtil: SystemPermissionUtil): NotificationProvider =
        NotificationsProviderImpl(systemPermissionUtil)

    @Provides
    @Singleton
    fun providesContactProvider(
        @ApplicationContext context: Context,
        permissionUtil: SystemPermissionUtil
    ): ContactsDataSource =
        ContactsDataSourceImpl(
            context,
            permissionUtil
        )

    @Provides
    @Singleton
    fun providesCallLogProvider(
        @ApplicationContext context: Context,
        systemPermissionUtil: SystemPermissionUtil
    ): CallLogDataSource =
        CallLogDataSourceImpl(
            context,
            systemPermissionUtil
        )

}