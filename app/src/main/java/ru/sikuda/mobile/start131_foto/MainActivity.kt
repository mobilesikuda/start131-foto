package ru.sikuda.mobile.start131_foto

import android.Manifest
import android.R.attr
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import ru.sikuda.mobile.start131_foto.databinding.ActivityMainBinding
import android.R.attr.bitmap
import android.graphics.Bitmap
import androidx.lifecycle.lifecycleScope
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding
    private var latestTmpUri: Uri? = null

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
    }

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                lifecycleScope.launchWhenStarted {
                    getTmpFileUri().let { uri ->
                        latestTmpUri = uri
                        takeImageResult.launch(uri)
                    }
                }
                // user granted permission
                //cameraShot.launch(null)
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

//    private val cameraShot = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
//        if (bitmap != null) {
//            //scale image
//            val bitmapScaled = Bitmap.createScaledBitmap(bitmap, binding.imageContainer.width, binding.imageContainer.height, true)
//            binding.imageContainer.setImageBitmap(bitmapScaled)
//            //setImageIsVisible(true)
//        } else {
//            // something was wrong
//            showToast(R.string.something_wrong)
//        }
//    }

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.imageContainer.setImageURI(uri)
            }
        } else {
            // something was wrong
            showToast(R.string.something_wrong)
        }
    }

    private fun showSettingsDialog() {
        //DontAskAgainFragment().show(parentFragmentManager, DontAskAgainFragment.TAG)
        showToast(R.string.denied_toast)
    }

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
}