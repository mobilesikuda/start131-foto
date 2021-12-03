package ru.sikuda.mobile.start131_foto

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import ru.sikuda.mobile.start131_foto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.cameraButton.setOnClickListener {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // we need to tell user why do we need permission
                showToast(R.string.need_permission)
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.closeButton.setOnClickListener {
            setImageIsVisible(false)
        }
    }


    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                // user granted permission
                cameraShot.launch(null)
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // user denied permission and set Don't ask again.
                showSettingsDialog()
            }
            else -> {
                showToast(R.string.denied_toast)
            }
        }
    }

    private val cameraShot = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {

            binding.imageContainer.setImageBitmap(bitmap)
            setImageIsVisible(true)
        } else {
            // something was wrong
            showToast(R.string.something_wrong)
        }
    }



    private fun showSettingsDialog() {
        //DontAskAgainFragment().show(parentFragmentManager, DontAskAgainFragment.TAG)
        showToast(R.string.denied_toast)
    }

    private fun setImageIsVisible(isVisible: Boolean) {
        binding.cameraButton.isVisible = !isVisible
        binding.closeButton.isVisible = isVisible
        binding.imageContainer.isVisible = isVisible
    }

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }

}