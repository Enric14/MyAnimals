package com.enric.myanimals.ui.changespet

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import com.enric.myanimals.databinding.ActivityChangesBinding
import com.enric.myanimals.domain.model.Pet
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.enric.myanimals.ui.detailspet.DetailsActivity
import com.enric.myanimals.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangesBinding
    private val viewModel: ChangesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

        val petId = intent.getStringExtra("pet")
        Log.d("ChangesActivity", "Pet ID recibido: $petId")
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
        initUIState()
        initListeners()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.pet?.let { pet ->
                        binding.apply {
                            activityChangesPetEtName.setText(pet.name)
                            activityChangesPetEtChip.setText(pet.chip)
                            activityChangesPetEtSpecies.setText(pet.species)
                            activityChangesPetEtBornDate.setText(pet.bornDate)
                            activityChangesPetEtBreed.setText(pet.breed)
                        }
                        showImage(pet.imageUrl)
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.activityChangesPetIv.setOnClickListener {
            showDiscardChangesDialog()
        }
    }

    private fun showImage(imageUrl: String) {
        Log.d("ChangesActivity", "URL de la imagen: $imageUrl")
        binding.apply {
            Glide.with(this@ChangesActivity)
                .load(imageUrl)
                .into(activityChangesPetCvIv)
            Log.d("ChangesActivity", "Imagen cargada con URL: $imageUrl")
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun showDiscardChangesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Quieres descartar los cambios?")
            .setPositiveButton("Sí") { dialog, _ ->
                navigateToHome()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }
}