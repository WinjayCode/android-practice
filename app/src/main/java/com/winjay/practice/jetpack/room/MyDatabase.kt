package com.winjay.practice.jetpack.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.winjay.practice.AppApplication

/**
 * 数据库
 *
 * @author Winjay
 * @date 2022-10-26
 */
@Database(entities = [User::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: MyDatabase? = null

        @Synchronized
        fun getDatabase(): MyDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    AppApplication.getApplication(),
                    MyDatabase::class.java,
                    "my_database"
                ).build()
            }
            return INSTANCE
        }
    }
}