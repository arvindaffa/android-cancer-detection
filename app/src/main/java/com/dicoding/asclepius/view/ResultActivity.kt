package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val result: String? by lazy { intent.getStringExtra(EXTRA_RESULT) }
    private val imageUri: Uri by lazy { Uri.parse(intent.getStringExtra(EXTRA_IMAGE)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        with(binding) {
            resultImage.setImageURI(imageUri)
            resultText.text = result
        }
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_RESULT = "extra_result"
    }
}