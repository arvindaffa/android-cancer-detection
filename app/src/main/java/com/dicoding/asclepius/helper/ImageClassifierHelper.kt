package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.IOException


class ImageClassifierHelper(
    private val threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val context: Context,
    private var imageClassifier: ImageClassifier? = null,
    val classifierListener: ClassifierListener
) {

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        // TODO: Menyiapkan Image Classifier untuk memproses gambar.
        try {
            val options = ImageClassifier.ImageClassifierOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)
                .setBaseOptions(BaseOptions.builder().setNumThreads(4).build())
                .build()

            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                MODEL_NAME,
                options
            )
        } catch (e: IllegalStateException) {
            classifierListener.onError("Image Classification Failed")
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        // TODO: mengklasifikasikan imageUri dari gambar statis.
        try {
            setupImageClassifier()
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                }.copy(Bitmap.Config.ARGB_8888, true)
            } catch (e: IOException) {
                null
            }

            val results = imageClassifier?.classify(
                ImageProcessor.Builder()
                    .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                    .add(CastOp(DataType.FLOAT32))
                    .build().process(TensorImage.fromBitmap(bitmap))
            )

            classifierListener.onResults(results)
        } catch (e: Exception) {
            classifierListener.onError(e.message.toString())
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?
        )
    }

    companion object {
        private const val MODEL_NAME = "cancer_classification.tflite"
    }
}