package com.example.finalexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalexam.data.Item
import com.example.finalexam.data.ItemRepository
import com.example.finalexam.data.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val itemRepository: ItemRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    val allItems: Flow<List<Item>> = itemRepository.getAllItems()
    val lastJoke: Flow<String> = userPreferencesRepository.lastJoke
    val isLoggedIn: Flow<Boolean> = userPreferencesRepository.isLoggedIn
    val username: Flow<String> = userPreferencesRepository.username
    
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory
    
    fun getItemsByCategory(category: String): Flow<List<Item>> {
        return if (category == "All") {
            itemRepository.getAllItems()
        } else {
            itemRepository.getItemsByCategory(category)
        }
    }
    
    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }
    
    fun getItem(id: Int): Flow<Item?> = itemRepository.getItem(id)
    
    fun insertItem(name: String, category: String, price: Double, quantity: Int) {
        viewModelScope.launch {
            val item = Item(
                name = name,
                category = category,
                price = price,
                quantity = quantity
            )
            itemRepository.insertItem(item)
        }
    }
    
    fun updateItem(id: Int, name: String, category: String, price: Double, quantity: Int) {
        viewModelScope.launch {
            val item = Item(
                id = id,
                name = name,
                category = category,
                price = price,
                quantity = quantity
            )
            itemRepository.updateItem(item)
        }
    }
    
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.deleteItem(item)
        }
    }
    
    fun login(username: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLoggedIn(true, username)
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.setLoggedIn(false, "")
        }
    }
}
