# ImageCachingApp

An Android application demonstrating efficient image loading with a custom two-tier caching system (memory + disk). Built as a multi-module project that separates the reusable image loading library from the application layer.

## Features

- Fetch image list from a remote API
- Display images in a scrollable RecyclerView
- Custom image loader with memory and disk caching
- Bitmap downsampling to reduce memory usage
- Image placeholder while loading
- Cache invalidation support
- Coroutine-based asynchronous image loading

## Architecture

The project consists of two modules:

### `imageloader` — Reusable library module

| Class | Responsibility |
|-------|---------------|
| `SimpleImageLoader` | Public API — singleton + builder pattern for loading images into views |
| `ImageCacheManager` | Two-tier cache (LRU memory + disk with TTL) |
| `ImageFetcher` | Interface for pluggable network fetch implementations |

### `app` — Application module

| Package | Classes |
|---------|---------|
| `data` | `ImageApiService`, `ImageRepository`, `ImageItem`, `OkHttpImageFetcher` |
| `ui` | `MainActivity`, `ImageViewModel`, `ImageAdapter`, `ImageDiffCallback` |

### Image loading flow

```
ImageAdapter
  └─► SimpleImageLoader.load(url).placeholder(R.drawable.ic_placeholder).into(imageView)
        └─► Check memory cache (LruCache)
              ├─► Hit → display immediately
              └─► Miss → check disk cache (TTL: 4h)
                    ├─► Hit → load to memory → display
                    └─► Miss → OkHttpImageFetcher.fetch()
                          └─► Download → downsample (max 1024px) → cache → display
```

## Caching Details

**Memory cache**
- Android `LruCache<String, Bitmap>`
- Size: 1/4 of available heap memory
- Key: MD5 hash of image URL

**Disk cache**
- Location: `context.cacheDir/image_cache/`
- Two files per entry: `{md5}.cache` (PNG bitmap) + `{md5}.meta` (timestamp)
- TTL: 4 hours — expired entries are automatically removed on next access

## Usage — `SimpleImageLoader`

Initialize once in `Application.onCreate()` or before first use:

```kotlin
SimpleImageLoader.init(context, OkHttpImageFetcher())
```

Load an image into a view:

```kotlin
SimpleImageLoader.getInstance()
    .load(imageUrl)
    .placeholder(R.drawable.ic_placeholder)
    .into(imageView)
```

Invalidate a cached entry:

```kotlin
SimpleImageLoader.getInstance().invalidateCache(imageUrl)
```

Clear the entire cache:

```kotlin
SimpleImageLoader.getInstance().clearCache()
```

## Tech Stack

| Technology | Version |
|-----------|---------|
| Kotlin | 2.3.20 |
| Android Gradle Plugin | 9.2.1 |
| Kotlin Coroutines | 1.7.3 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| AndroidX Lifecycle (ViewModel/LiveData) | 2.7.0 |
| RecyclerView | 1.3.2 |
| Material Components | 1.11.0 |

## Requirements

- Android Studio (latest stable recommended)
- Min SDK: 30
- Target SDK: 34
- Internet permission is declared in the manifest

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Let Gradle sync
4. Run the `app` module on a device or emulator (API 30+)

## Project Structure

```
ImageCachingApp/
├── app/
│   └── src/main/java/com/amilojev86/imageCachingApp/
│       ├── data/
│       │   ├── ImageApiService.kt
│       │   ├── ImageItem.kt
│       │   ├── ImageRepository.kt
│       │   └── OkHttpImageFetcher.kt
│       └── ui/
│           ├── ImageAdapter.kt
│           ├── ImageDiffCallback.kt
│           ├── ImageViewModel.kt
│           └── MainActivity.kt
├── imageloader/
│   └── src/main/java/com/imageloader/
│       ├── ImageCacheManager.kt
│       ├── ImageFetcher.kt
│       └── SimpleImageLoader.kt
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml
```
