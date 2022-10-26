package com.winjay.practice.jetpack.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * 数据库
 *
 * @author Winjay
 * @date 2022-10-26
 */
@Database(entities = [User::class], version = 1)
abstract class MyDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}