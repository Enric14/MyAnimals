package com.enric.myanimals.ui.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.enric.myanimals.databinding.ItemPetBinding
import com.enric.myanimals.domain.model.Pet

class PetsViewHolder(
    view: View,
    private val deleteListener: (Pet) -> Unit,
    private val detailListener: (Pet) -> Unit
) : RecyclerView.ViewHolder(view) {

    val binding = ItemPetBinding.bind(view)

    fun render(pet: Pet) {
        binding.apply {
            Glide.with(binding.itemPetTv.context).load(pet.imageUrl).into(itemPetIv)
            itemPetTv.text = pet.name
            itemPetIvDelete.setOnClickListener {
                deleteListener.invoke(pet)
            }
            itemPetGoDetail.setOnClickListener {
                detailListener.invoke(pet)
            }
        }
    }
}