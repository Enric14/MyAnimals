package com.enric.myanimals.ui.detailspet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.enric.myanimals.databinding.ActivityDetailsBinding
import com.enric.myanimals.domain.model.Pet
import com.enric.myanimals.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

        val petId = intent.getStringExtra("pet")
        Log.d("DetailsActivity", "Pet ID recibido: $petId")
        petId?.let {
            viewModel.getPetDetails(
                Pet(
                    id = it, name = "", species = "", breed = "",
                    chip = "", bornDate = "", imageUrl = "", uid = ""
                )
            )
        }
    }

    private fun initUI() {
        initListeners()
        initUIState()
    }
    
    private fun initListeners() {
        binding.activityDetailsPetIv.setOnClickListener {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun initUIState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.pet?.let { pet ->
                        binding.apply {
                            activityDetailsPetEtName.setText(pet.name)
                            activityDetailsPetEtChip.setText(pet.chip)
                            activityDetailsPetEtSpecies.setText(pet.species)
                            activityDetailsPetEtBornDate.setText(pet.bornDate)
                            activityDetailsPetEtBreed.setText(pet.breed)
                        }
                        showImage(pet.imageUrl)
                    }
                }
            }
        }
    }

    private fun showImage(imageUrl: String) {
        Log.d("DetailsActivity", "URL de la imagen: $imageUrl")
        binding.apply {
            Glide.with(this@DetailsActivity)
                .load(imageUrl)
                .into(activityDetailsPetCvIv)
            Log.d("DetailsActivity", "Imagen cargada con URL: $imageUrl")
        }
    }
}