package com.enric.myanimals.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.enric.myanimals.R
import com.enric.myanimals.databinding.ActivityHomeBinding
import com.enric.myanimals.domain.model.Pet
import com.enric.myanimals.ui.addpet.AddPetActivity
import com.enric.myanimals.ui.detailspet.DetailsActivity
import com.enric.myanimals.ui.home.adapter.PetsAdapter
import com.enric.myanimals.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var petsAdapter: PetsAdapter

    private val addPetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        initUI()
    }

    private fun initUI() {
        initListeners()
        initList()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderPets(state.pets)
                }
            }
        }
    }

    private fun initList() {
        petsAdapter = PetsAdapter(
            deleteListener = { pet ->
                showDeleteConfirmationDialog(pet)
            },
            detailListener = { pet ->
                navigateToDetails(pet)
            }
        )
        binding.activityHomeRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = petsAdapter
        }
    }

    private fun navigateToDetails(pet: Pet) {
        startActivity(Intent(this, DetailsActivity::class.java).putExtra("pet", pet.id).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun renderPets(pets: MutableList<Pet>) {
        petsAdapter.updateList(pets)
    }


    private fun initListeners() {
        binding.activityHomeTb.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_log_out -> {
                    viewModel.logout {
                        showLogoutConfirmationDialog()
                    }
                    true
                }
                R.id.menu_add_animal -> {
                    showAddPetConfirmationDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showAddPetConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Añadir mascota")
            .setMessage("¿Quieres añadir una mascota?")
            .setPositiveButton("Sí") { dialog, which ->
                addPetLauncher.launch(AddPetActivity.create(this))
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Cerrar Sesión")
        alertDialogBuilder.setMessage("¿Quieres cerrar la sesión?")
        alertDialogBuilder.setPositiveButton("Sí") { _, _ ->
            logout()
        }
        alertDialogBuilder.setNegativeButton("No", null)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showDeleteConfirmationDialog(pet: Pet) {
        val context = binding.root.context
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar Mascota")
            .setMessage("¿Quieres eliminar esta mascota?")
            .setPositiveButton("Sí") { dialog, which ->
                viewModel.deletePet(pet)
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        viewModel.logout {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}