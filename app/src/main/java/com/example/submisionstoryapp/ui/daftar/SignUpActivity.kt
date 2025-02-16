package com.example.submisionstoryapp.ui.daftar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.submisionstoryapp.api.RegistResponse
import com.example.submisionstoryapp.databinding.ActivitySignUpBinding
import com.example.submisionstoryapp.ui.helper.ViewModelFactory
import com.example.submisionstoryapp.data.Result


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    // ViewModel Initialization
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    // Initialize All UI Components
    private fun initUI() {
        setupView()
        setupAction()
        playAnimation()
    }

    // Configure Fullscreen and Hide ActionBar
    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    // Handle User Actions (Signup Button)
    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            handleSignup()
        }
    }

    // Process User Signup
    private fun handleSignup() {
        val name = binding.nameInput.text.toString()
        val email = binding.emailFields.text.toString()
        val password = binding.passwordFields.text.toString()

        viewModel.signup(name, email, password).observe(this) { result ->
            when (result) {
                is com.example.submisionstoryapp.data.Result.Load -> {
                    showLoading(true)
                }
                is com.example.submisionstoryapp.data.Result.Success -> {
                    showLoading(false)
                    handleSuccess(result.data)
                }
                is com.example.submisionstoryapp.data.Result.Error -> {
                    showLoading(false)
                    showToast(result.massage)
                }
            }
        }
    }

    // Handle Success Response
    private fun handleSuccess(data: RegistResponse) {
        if (data.error == true) {
            showToast(data.message)
        } else {
            showToast(data.message)
            finish()
        }
    }

    // Show or Hide Loading
    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Show Toast Messages
    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
    }

    // Play UI Animations
    private fun playAnimation() {
        animateImageView()
        animateSequentialViews()
    }

    // Image Animation (Horizontal Movement)
    private fun animateImageView() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    // Sequential Fade-In Animation for UI Components
    private fun animateSequentialViews() {
        val duration = 100L

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(duration)
        val nameText = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(duration)
        val emailText = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(duration)
        val nameInput = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(duration)
        val emailInput = ObjectAnimator.ofFloat(binding.emailFields, View.ALPHA, 1f).setDuration(duration)
        val passwordText = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(duration)
        val passwordInput = ObjectAnimator.ofFloat(binding.passwordFields, View.ALPHA, 1f).setDuration(duration)
        val signupButton = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(duration)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameText,
                emailText,
                nameInput,
                emailInput,
                passwordText,
                passwordInput,
                signupButton
            )
            start()
        }
    }
}