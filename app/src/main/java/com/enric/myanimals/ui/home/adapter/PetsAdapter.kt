package com.enric.myanimals.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enric.myanimals.R
import com.enric.myanimals.domain.model.Pet
import com.enric.myanimals.ui.home.HomeActivity

class PetsAdapter(
    private var pets: MutableList<Pet> = mutableListOf(),
    private val deleteListener: (Pet) -> Unit,
    private val detailListener: (Pet) -> Unit,
) : RecyclerView.Adapter<PetsViewHolder>() {

    fun updateList(pets: MutableList<Pet>) {
        this.pets = pets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetsViewHolder(view, deleteListener, detailListener)
    }

    override fun getItemCount(): Int = pets.size


    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        holder.render(pets[position])
    }
}