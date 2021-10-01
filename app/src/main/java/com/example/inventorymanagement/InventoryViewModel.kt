package com.example.inventorymanagement

import androidx.lifecycle.*
import com.example.inventorymanagement.data.Item
import com.example.inventorymanagement.data.ItemDao
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao): ViewModel() {

    val allItems : LiveData<List<Item>> = itemDao.getAllItems().asLiveData()

    fun getItemById(id : Int) : LiveData<Item> = itemDao.getItemById(id).asLiveData()

    private fun insertItem(item : Item){
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    fun updateItem(item: Item){ viewModelScope.launch { itemDao.update(item) } }

    fun deleteItem(item : Item){ viewModelScope.launch{ itemDao.delete(item) } }

    fun isStockAvailable(item: Item) : Boolean{
        return (item.quantityInStock > 0)
    }

    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

}

class InventoryViewModelFactory(private val itemDao: ItemDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if ( modelClass.isAssignableFrom(InventoryViewModel::class.java) ){
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }
}