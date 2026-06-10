package com.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class ImageCacheManager(context: Context) {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 4

    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    private val diskCacheDir: File = File(context.cacheDir, "image_cache").apply { mkdirs() }
    private val FOUR_HOURS_MS = 4 * 60 * 60 * 1000L

    fun get(url: String): Bitmap? {
        val key = sanitizeKey(url)

        memoryCache.get(key)?.let { return it }

        val bitmapFile = File(diskCacheDir, "$key.cache")
        val metaFile = File(diskCacheDir, "$key.meta")

        if (bitmapFile.exists() && metaFile.exists()) {
            try {
                val timestamp = metaFile.readText().toLong()
                if (System.currentTimeMillis() - timestamp > FOUR_HOURS_MS) {
                    invalidate(url)
                    return null
                }
                val bitmap = BitmapFactory.decodeFile(bitmapFile.absolutePath)
                if (bitmap != null) {
                    memoryCache.put(key, bitmap)
                    return bitmap
                }
            } catch (e: Exception) {
                return null
            }
        }

        return null
    }

    fun put(url: String, bitmap: Bitmap) {
        val key = sanitizeKey(url)
        memoryCache.put(key, bitmap)
        try {
            val bitmapFile = File(diskCacheDir, "$key.cache")
            val metaFile = File(diskCacheDir, "$key.meta")
            FileOutputStream(bitmapFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            metaFile.writeText(System.currentTimeMillis().toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun invalidate(url: String) {
        val key = sanitizeKey(url)
        memoryCache.remove(key)
        File(diskCacheDir, "$key.cache").delete()
        File(diskCacheDir, "$key.meta").delete()
    }

    fun clearAll() {
        memoryCache.evictAll()
        diskCacheDir.deleteRecursively()
        diskCacheDir.mkdirs()
    }

    private fun sanitizeKey(url: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(url.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
