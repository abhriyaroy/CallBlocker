package com.abhriya.callblocker.data

import com.abhriya.callblocker.data.entity.ContactEntity
import com.abhriya.callblocker.data.exception.DataLayerException
import com.abhriya.callblocker.data.mapper.ContactEntityMapper
import com.abhriya.contactsprovider.ContactsProvider
import com.abhriya.blockedcontactsdatabase.DatabaseHelper
import com.abhriya.blockedcontactsdatabase.exception.DatabaseException

interface ContactsRepository {
    suspend fun saveBlockedContact(contactEntity: ContactEntity)
    suspend fun unBlockContact(contactEntity: ContactEntity)
    suspend fun getAllBlockedContacts(): List<ContactEntity>
    suspend fun getAllContactsFromDevice(): List<ContactEntity>
}

class ContactsRepositoryImpl(
    private val databaseHelper: DatabaseHelper,
    private val contactsProvider: ContactsProvider
) : ContactsRepository {
    override suspend fun saveBlockedContact(contactEntity: ContactEntity) {
        try {
            contactEntity.let {
                ContactEntityMapper.mapToDbContactEntityFromContactEntity(it)
            }.also {
                databaseHelper.saveToBlockedContactsDb(it)
            }
        } catch (databaseException: DatabaseException) {
            throw DataLayerException(databaseException.message)
        }
    }

    override suspend fun unBlockContact(contactEntity: ContactEntity) {
        try {
            contactEntity.let {
                ContactEntityMapper.mapToDbContactEntityFromContactEntity(it)
            }.also {
                databaseHelper.removeBlockedContactsFromDb(it)
            }
        } catch (databaseException: DatabaseException) {
            throw DataLayerException(databaseException.message)
        }
    }

    override suspend fun getAllBlockedContacts(): List<ContactEntity> {
        try {
            return databaseHelper.getAllBlockedContacts()
                .map {
                    ContactEntityMapper.mapToContactEntityFromDbContactEntity(it)
                }
        } catch (databaseException: DatabaseException) {
            throw DataLayerException(databaseException.message)
        }
    }

    override suspend fun getAllContactsFromDevice(): List<ContactEntity> {
        println("contacts repositroy")
        return contactsProvider.getAllContactsFromDevice()
            .map {
                ContactEntityMapper.mapToContactEntityFromDeviceContactEntity(it)
            }
    }
}