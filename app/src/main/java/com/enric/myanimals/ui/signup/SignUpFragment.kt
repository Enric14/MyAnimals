package com.enric.myanimals.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.enric.myanimals.R
import com.enric.myanimals.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel.signUpState.observe(viewLifecycleOwner, Observer { result ->
            result.fold(
                onSuccess = { user ->
                    Toast.makeText(
                        requireContext(),
                        "Usuario registrado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                },
                onFailure = {
                    handleError()
                }
            )
        })
    }

    private fun handleError() {
        Toast.makeText(requireContext(), "Error al registrarse", Toast.LENGTH_SHORT)
            .show()
    }

    private fun initListeners() {
        with(binding) {
            fragmentSignUpMb.setOnClickListener {
                handleSignUp()
            }
            fragmentSignUpIv.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
        }
    }

    private fun handleSignUp() {
        val email = binding.fragmentSignUpTietUser.text.toString()
        val password = binding.fragmentSignUpTietPasswordOne.text.toString()
        val confirmPassword = binding.fragmentSignUpTietPasswordTwo.text.toString()

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Por favor, completa todos los campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(
                requireContext(),
                "Las contraseñas no coinciden",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.signUp(email, password)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
