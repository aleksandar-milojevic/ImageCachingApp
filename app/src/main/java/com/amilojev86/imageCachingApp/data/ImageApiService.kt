package com.amilojev86.imageCachingApp.data

import retrofit2.http.GET

interface ImageApiService {
    @GET("image_list.json")
    suspend fun getImages(): List<ImageItem>
}
