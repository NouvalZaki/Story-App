package com.example.submisionstoryapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submisionstoryapp.databinding.ActivityMainBinding
import com.example.submisionstoryapp.ui.helper.LoadingStateAdapter
import com.example.submisionstoryapp.ui.helper.StoryAdapter
import com.example.submisionstoryapp.ui.helper.ViewModelFactory
import com.example.submisionstoryapp.ui.login.LoginActivity
import com.example.submisionstoryapp.ui.welcome.WelcomeActivity
import com.example.submisionstoryapp.ui.maps.MapsActivity
import kotlinx.coroutines.launch
import com.example.submisionstoryapp.ui.story.NewStoryActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private val storyAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.show()

        observeSession()
        setupRecyclerView()
        setupAction()
        observeStories()
    }

    private fun observeSession() {
        // Check user login status
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvStory.apply {
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeStories() {
        lifecycleScope.launch {
            viewModel.stories.observe(this@MainActivity) { pagingData ->
                storyAdapter.submitData(lifecycle, pagingData)
            }
        }

        storyAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is androidx.paging.LoadState.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }

                is androidx.paging.LoadState.NotLoading -> {
                    binding.loading.visibility = View.GONE
                }

                is androidx.paging.LoadState.Error -> {
                    binding.loading.visibility = View.GONE
                    val errorState = loadState.refresh as androidx.paging.LoadState.Error
                    Toast.makeText(this, errorState.error.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, NewStoryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }


        binding.btnMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Logout")
            setMessage("Are you sure you want to log out?")
            setPositiveButton("Yes") { _, _ -> logout() }
            setNegativeButton("Cancel", null)
            create()
            show()
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            viewModel.logout()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
