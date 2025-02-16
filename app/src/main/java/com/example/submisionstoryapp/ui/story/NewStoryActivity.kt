package com.example.submisionstoryapp.ui.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.submisionstoryapp.R
import com.example.submisionstoryapp.databinding.ActivityNewStoryBinding
import com.example.submisionstoryapp.ui.helper.ViewModelFactory
import com.example.submisionstoryapp.ui.helper.getImageUri
import com.example.submisionstoryapp.ui.helper.reduceFileImage
import com.example.submisionstoryapp.ui.helper.uriToFile
import com.example.submisionstoryapp.ui.main.MainActivity
import com.example.submisionstoryapp.data.Result
import com.example.submisionstoryapp.ui.main.MainViewModel

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this, REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnGals.setOnClickListener { startGallery() }
        binding.btnCams.setOnClickListener { startCamera() }
        binding.btnSubmit.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        val imageUri = getImageUri(this)
        currentImageUri = imageUri
        launcherIntentCamera.launch(imageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.pict.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "File path: ${imageFile.path}")
            val description = binding.descTextEdit.text.toString()

            if (description.isBlank()) {
                showToast(getString(R.string.error))
                return@let
            }

            viewModel.getSession().observe(this) { story ->
                val token = story.token
                viewModel.uploadStory(token, imageFile, description).observe(this) { result ->
                    when (result) {
                        is Result.Load -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            showToast(result.data.message)
                            startActivity(Intent(this@NewStoryActivity, MainActivity::class.java))
                            finish()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showToast(result.massage) // Correcting the typo in Result.Error
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.error))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
