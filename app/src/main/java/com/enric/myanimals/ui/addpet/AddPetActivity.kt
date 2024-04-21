package com.enric.myanimals.ui.addpet

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.enric.myanimals.databinding.ActivityAddPetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@AndroidEntryPoint
class AddPetActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent {
            return Intent(context, AddPetActivity::class.java)
        }
    }

    private lateinit var uri:Uri

    private lateinit var binding:ActivityAddPetBinding
    private val addPetViewModel:AddPetViewModel by viewModels()

    private val intentCameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it && uri.path?.isNotEmpty() == true) {
                addPetViewModel.onImageSelected(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initUIState()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addPetViewModel.uiState.collect {
                    binding.activityAddPetPb.isVisible = it.isLoading
                    binding.activityAddPetBtn.isEnabled = it.isValidPet()
                    showImage(it.imageUrl)
                    if(!it.error.isNullOrBlank()) {

                    }
                }
            }
        }
    }

    private fun showImage(imageUrl: String) {
        val emptyImage = imageUrl.isEmpty()
        Log.d("AddPetActivity", "URL de la imagen: $imageUrl")
        binding.apply {
            activityAddPetTvImage.isVisible = emptyImage
            activityAddPetEtImage.isVisible = emptyImage
            activityAddPetCv.isVisible = !emptyImage
            Log.d("AddPetActivity", "emptyImage: $emptyImage")
            Glide.with(this@AddPetActivity).load(imageUrl).into(activityAddPetCvIv)
            Log.d("AddPetActivity", "Imagen cargada con URL: $imageUrl")
        }
    }

    private fun initListeners() {
        binding.activityAddPetEtName.doOnTextChanged { text, _, _, _ ->
            addPetViewModel.onNameChanged(text)
        }

        binding.activityAddPetEtChip.doOnTextChanged { text, _, _, _ ->
            addPetViewModel.onChipChanged(text)
        }

        binding.activityAddPetEtSpecies.doOnTextChanged { text, _, _, _ ->
            addPetViewModel.onSpeciesChanged(text)
        }

        binding.activityAddPetEtBornDate.doOnTextChanged { text, _, _, _ ->
            addPetViewModel.onBornDateChanged(text)
        }

        binding.activityAddPetEtBreed.doOnTextChanged { text, _, _, _ ->
            addPetViewModel.onBreedChanged(text)
        }

        binding.activityAddPetEtImage.setOnClickListener {
            takePhoto()
        }

        binding.activityAddPetIv.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.activityAddPetBtn.setOnClickListener {
            addPetViewModel.onAddPetSelected {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun takePhoto() {
        generateUri()
        Log.d("AddPetActivity", "URI de la imagen generada: $uri")
        intentCameraLauncher.launch(uri)

    }

    private fun generateUri() {
        uri = FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.enric.myanimals.provider",
            createFile()
        )
    }

    private fun createFile(): File {
        val name: String = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date()) + "image"
        return File.createTempFile(name, ".jpg", externalCacheDir)
    }
}