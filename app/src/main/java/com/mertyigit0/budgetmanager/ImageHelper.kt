package com.mertyigit0.budgetmanager

import android.content.Intent
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.IOException

class ImageHelper(private val activity: Activity) {

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    // Galeriden fotoğrafı çekip, alınan fotoğrafın URI'sini döndüren fonksiyon
    fun takePhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Image URI'ini byte dizisine dönüştüren fonksiyon
    fun imageUriToByteArray(uri: Uri): ByteArray? {
        val inputStream = activity.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        return bitmapToByteArray(bitmap)
    }

    // Bitmap'i byte dizisine dönüştüren fonksiyon
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
