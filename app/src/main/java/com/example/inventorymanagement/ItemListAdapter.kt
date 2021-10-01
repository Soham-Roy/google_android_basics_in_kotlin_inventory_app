package com.example.inventorymanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanagement.data.Item
import com.example.inventorymanagement.databinding.ItemListItemBinding

class ItemListAdapter(private val listener: ItemClickListener) : ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallBack) {

    companion object DiffCallBack : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return ( oldItem.id == newItem.id )
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return ( oldItem == newItem )
        }
    }

    inner class ItemViewHolder(val bind : ItemListItemBinding) : RecyclerView.ViewHolder(bind.root){
        fun bindValues(item: Item){
            with(bind) {
                itemName.text = item.itemName
                itemPrice.text = item.itemPrice.toString()
                itemQuantity.text = item.quantityInStock.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bindValues(current)
        holder.itemView.setOnClickListener {
            listener.onItemClicked(holder.adapterPosition)
        }
    }

}

interface ItemClickListener{
    fun onItemClicked(position: Int)
}