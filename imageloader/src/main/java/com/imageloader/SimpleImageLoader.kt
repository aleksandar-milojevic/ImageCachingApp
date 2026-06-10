package com.imageloader

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import kotlinx.coroutines.*

class SimpleImageLoader(
    private val cache: ImageCacheManager,
    private val fetcher: ImageFetcher
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun load(url: String): RequestBuilder = RequestBuilder(url)

    fun invalidateCache() = cache.clearAll()

    inner class RequestBuilder(private val url: String) {

        @DrawableRes private var placeholderRes: Int? = null

        fun placeholder(@DrawableRes resId: Int) = apply { placeholderRes = resId }

        fun into(target: ImageView) {
            placeholderRes?.let { target.setImageResource(it) }
            target.tag = url
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    cache.get(url) ?: fetcher.fetch(url)?.also {
                        cache.put(url, it)
                    }
                }
                if (target.tag == url && bitmap != null) {
                    target.setImageBitmap(bitmap)
                }
            }
        }
    }

    companion object {

        @Volatile private var instance: SimpleImageLoader? = null

        @JvmStatic
        fun init(context: Context, fetcher: ImageFetcher) {
            instance ?: synchronized(this) {
                instance ?: SimpleImageLoader(
                    cache = ImageCacheManager(context.applicationContext),
                    fetcher = fetcher
                ).also { instance = it }
            }
        }

        @JvmStatic
        fun getInstance(): SimpleImageLoader =
            checkNotNull(instance)
    }
}
