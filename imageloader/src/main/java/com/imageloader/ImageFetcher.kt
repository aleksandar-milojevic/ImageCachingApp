package com.imageloader

import android.graphics.Bitmap

interface ImageFetcher {
    suspend fun fetch(url: String): Bitmap?
}
