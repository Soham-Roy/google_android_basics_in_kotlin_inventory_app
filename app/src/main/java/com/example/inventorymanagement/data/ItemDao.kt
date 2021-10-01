package com.example.inventorymanagement.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems() : Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Int) : Flow<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

}