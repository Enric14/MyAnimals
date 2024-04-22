package com.enric.myanimals.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.enric.myanimals.R
import com.enric.myanimals.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.loginState.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(requireContext(), "Accediendo...", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_welcomeActivity)
                },
                onFailure = {
                    handleError()
                }
            )
        }
    }
    private fun emptyFields() {
        binding.apply {
            val user = fragmentLoginTietUser.toString().trim()
            val password = fragmentLoginTietPassword.toString().trim()

            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Uno o ambos elementos están vacíos", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun handleError() {
        binding.apply {
            val user = fragmentLoginTietUser.text.toString().trim()
            val password = fragmentLoginTietPassword.text.toString().trim()

            if (user.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Correo electrónico y/o contraseña vacíos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Usuario no registrado", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            fragmentLoginTvOne.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }
            fragmentLoginMb.setOnClickListener {
                handleLogin()
            }
            fragmentLoginTvThree.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_passwordRecoveryFragment)
            }
        }
    }

    private fun handleLogin() {
        val email = binding.fragmentLoginTietUser.text.toString()
        val password = binding.fragmentLoginTietPassword.text.toString()

        viewModel.login(email, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}