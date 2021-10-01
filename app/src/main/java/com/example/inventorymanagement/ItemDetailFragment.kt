/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventorymanagement


import android.content.ClipData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventorymanagement.data.Item
import com.example.inventorymanagement.databinding.FragmentItemDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * [ItemDetailFragment] displays the details of the selected item.
 */
class ItemDetailFragment : Fragment() {
    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentItem: Item

    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getItemById(navigationArgs.itemId).observe(viewLifecycleOwner){
            currentItem = it
            with(binding){
                itemName.text = it.itemName
                itemPrice.text = it.itemPrice.toString()
                itemCount.text = it.quantityInStock.toString()
            }
            binding.sellItem.isEnabled = viewModel.isStockAvailable(currentItem)
        }
        binding.apply {
            sellItem.setOnClickListener { sellItem() }
            deleteItem.setOnClickListener { showConfirmationDialog() }
            editItem.setOnClickListener { editItem(currentItem.id) }
        }
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    private fun sellItem(){
        if ( currentItem.quantityInStock <= 0 ){
            return
        }
        val newItem = currentItem.copy(quantityInStock = currentItem.quantityInStock-1)
        viewModel.updateItem(newItem)
    }

    private fun editItem(id: Int){
        val action = ItemDetailFragmentDirections.actionItemDetailFragmentToAddItemFragment(
            getString(R.string.edit_fragment_title),
            itemId = id
        )
        findNavController().navigate(action)
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        viewModel.deleteItem(currentItem)
        findNavController().navigateUp()
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
