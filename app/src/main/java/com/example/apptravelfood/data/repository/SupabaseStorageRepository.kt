package com.example.apptravelfood.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.apptravelfood.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import java.util.UUID

class SupabaseStorageRepository {
    private suspend fun uploadImage(
        context: Context,
        bucket: String,
        folder: String,
        imageUri: Uri,
        fileName: String? = null
    ): String {
        return try {
            val bytes = context.contentResolver
                .openInputStream(imageUri)
                ?.use { it.readBytes() }
                ?: throw Exception("Không đọc được ảnh")

            Log.d("SUPABASE_UPLOAD", "bytes=${bytes.size}")

            val finalFileName = fileName ?: "${UUID.randomUUID()}.jpg"
            val path = "$folder/$finalFileName"

            Log.d("SUPABASE_UPLOAD", "bucket=$bucket")
            Log.d("SUPABASE_UPLOAD", "path=$path")

            SupabaseClientProvider.client.storage
                .from(bucket)
                .upload(path, bytes) {
                    upsert = true
                    contentType = ContentType.Image.JPEG
                }

            Log.d("SUPABASE_UPLOAD", "upload success")

            val url = SupabaseClientProvider.client.storage
                .from(bucket)
                .publicUrl(path)

            Log.d("SUPABASE_UPLOAD", "url=$url")

            url

        } catch (e: Exception) {
            Log.e("SUPABASE_UPLOAD", "upload failed", e)
            throw e
        }
    }

    suspend fun uploadAvatar(
        context: Context,
        userId: Long,
        imageUri: Uri
    ): String {
        Log.d("SUPABASE_UPLOAD", "Start uri=$imageUri")
        return uploadImage(
            context = context,
            bucket = "avatars",
            folder = "user_$userId",
            imageUri = imageUri,
            fileName = "avatar_${System.currentTimeMillis()}.jpg" // Thêm timestamp để tránh cache
        )
    }

    suspend fun uploadFoodStoreImage(
        context: Context,
        foodStoreId: Long,
        imageUri: Uri
    ): String {
        return uploadImage(
            context = context,
            bucket = "food-stores",
            folder = "store_$foodStoreId",
            imageUri = imageUri
        )
    }

    suspend fun uploadFoodItemImage(
        context: Context,
        foodItemId: Long,
        imageUri: Uri
    ): String {
        return uploadImage(
            context = context,
            bucket = "food-items",
            folder = "item_$foodItemId",
            imageUri = imageUri
        )
    }

}