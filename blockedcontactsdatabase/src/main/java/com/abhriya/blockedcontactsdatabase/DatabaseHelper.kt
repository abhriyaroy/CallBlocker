package com.abhriya.blockedcontactsdatabase

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room
import com.abhriya.blockedcontactsdatabase.blockedcontact.BlockedContactsDatabase
import com.abhriya.blockedcontactsdatabase.entity.ContactDbEntity
import com.abhriya.blockedcontactsdatabase.exception.DatabaseException
import com.abhriya.blockedcontactsdatabase.mapper.ContactEntityMapper

interface DatabaseHelper {
    suspend fun saveToBlockedContactsDb(vararg contactDbToBlock: ContactDbEntity)
    suspend fun removeBlockedContactsFromDb(vararg contactDbToUnblock: ContactDbEntity)
    suspend fun getAllBlockedContacts(): List<ContactDbEntity>
    suspend fun getBlockedContactByNumber(phoneNumber: String): ContactDbEntity?
}

class DatabaseHelperImpl(applicationContext: Context) : DatabaseHelper {

    private val blockedContactsDb: BlockedContactsDatabase = Room.databaseBuilder(
        applicationContext,
        BlockedContactsDatabase::class.java, "blocked-contacts"
    ).build()

    override suspend fun saveToBlockedContactsDb(vararg contactDbToBlock: ContactDbEntity) {
        try {
            contactDbToBlock.map {
                ContactEntityMapper.mapToBlockedContactsDbEntityFromContactEntity(it)
            }.forEach {
                blockedContactsDb.contactsDao().insert(it)
            }
        } catch (sqLiteException: SQLiteException) {
            throw DatabaseException()
        }
    }

    override suspend fun removeBlockedContactsFromDb(vararg contactDbToUnblock: ContactDbEntity) {
        try {
            contactDbToUnblock.map {
                ContactEntityMapper.mapToBlockedContactsDbEntityFromContactEntity(it)
            }.forEach {
                blockedContactsDb.contactsDao().delete(it.phoneNumber)
            }
        } catch (sqLiteException: SQLiteException) {
            throw DatabaseException()
        }
    }

    override suspend fun getAllBlockedContacts(): List<ContactDbEntity> {
        try {
            return blockedContactsDb.contactsDao().getAll()
                .map {
                    ContactEntityMapper.mapToContactEntityFromBlockedContactsDbEntity(it)
                }
        } catch (sqLiteException: SQLiteException) {
            throw DatabaseException()
        }
    }

    override suspend fun getBlockedContactByNumber(phoneNumber: String): ContactDbEntity? {
        try {
            return blockedContactsDb.contactsDao().getAll()
                .find {
                    phoneNumber.contains(it.phoneNumber)
                }?.let {
                    ContactEntityMapper.mapToContactEntityFromBlockedContactsDbEntity(it)
                }
        } catch (sqLiteException: SQLiteException) {
            throw DatabaseException()
        }
    }
}