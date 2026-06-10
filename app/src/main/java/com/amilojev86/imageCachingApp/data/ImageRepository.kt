package com.amilojev86.imageCachingApp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageRepository {

    private val api: ImageApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ImageApiService::class.java)

    suspend fun fetchImages(): Result<List<ImageItem>> = runCatching { api.getImages() }

    companion object {
        private const val BASE_URL =
            "https://zipoapps-storage-test.nyc3.digitaloceanspaces.com/"
    }
}
