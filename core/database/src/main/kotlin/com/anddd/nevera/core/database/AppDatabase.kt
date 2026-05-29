package com.anddd.nevera.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anddd.nevera.core.database.dao.NotificationDao
import com.anddd.nevera.core.database.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
