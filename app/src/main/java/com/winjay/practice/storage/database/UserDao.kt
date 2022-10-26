package com.winjay.practice.storage.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * 数据访问对象 DAO
 *
 * @author Winjay
 * @date 2022-10-26
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    // vararg 在 kotlin 中表示为可变参数
    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}