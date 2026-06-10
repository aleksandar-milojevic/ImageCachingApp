package com.amilojev86.imageCachingApp.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.imageloader.ImageFetcher
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpImageFetcher : ImageFetcher {

    private val client = OkHttpClient()

    override suspend fun fetch(url: String): Bitmap? =
        runCatching {
            val bytes = client.newCall(Request.Builder().url(url).build())
                .execute()
                .body?.bytes() ?: error("Empty response body")

            BitmapFactory.Options()
                .apply { inJustDecodeBounds = true }
                .also { BitmapFactory.decodeByteArray(bytes, 0, bytes.size, it) }
                .run {
                    inSampleSize = calculateInSampleSize(this)
                    inJustDecodeBounds = false
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
                }
        }.getOrNull()

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int = MAX_DIMENSION, reqHeight: Int = MAX_DIMENSION): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1
        if (width > reqWidth || height > reqHeight) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqHeight) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {
        private const val MAX_DIMENSION = 1024
    }
}
