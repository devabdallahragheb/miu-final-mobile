package com.example.finalexam.data

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
    fun getAllItems(): Flow<List<Item>> = itemDao.getAllItems()
    
    fun getItemsByCategory(category: String): Flow<List<Item>> = 
        itemDao.getItemsByCategory(category)
    
    fun getItem(id: Int): Flow<Item?> = itemDao.getItem(id)
    
    suspend fun insertItem(item: Item) = itemDao.insert(item)
    
    suspend fun updateItem(item: Item) = itemDao.update(item)
    
    suspend fun deleteItem(item: Item) = itemDao.delete(item)
}
