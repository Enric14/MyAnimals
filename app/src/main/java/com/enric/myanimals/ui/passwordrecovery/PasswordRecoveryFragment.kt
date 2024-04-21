package com.enric.myanimals.ui.passwordrecovery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.enric.myanimals.R
import com.enric.myanimals.databinding.FragmentPasswordRecoveryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordRecoveryFragment : Fragment() {

    private var _binding:FragmentPasswordRecoveryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PasswordRecoveryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPasswordRecoveryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        with(binding) {
            fragmentPasswordRecoveryIv.setOnClickListener {
                findNavController().navigate(R.id.action_passwordRecoveryFragment_to_loginFragment)
            }
            fragmentPasswordRecoveryMb.setOnClickListener {
                val email = fragmentPasswordRecoveryTietUser.text.toString()
                if (email.isNotBlank()) {
                    viewModel.recoverPassword(email)
                } else {
                    Toast.makeText(requireContext(), "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.recoveryState.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    findNavController().navigate(R.id.action_passwordRecoveryFragment_to_loginFragment)
                    Toast.makeText(requireContext(), "Se ha enviado un correo electrónico para restablecer la contraseña", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Error al intentar recuperar la contraseña: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}