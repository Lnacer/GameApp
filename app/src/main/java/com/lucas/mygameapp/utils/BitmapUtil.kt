package com.lucas.mygameapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.lucas.mygameapp.view.MainActivity

class BitmapUtil {
    companion object {
        fun generateBitmap(imagePath : String) : Bitmap {
            val bitmap = BitmapFactory.decodeFile(imagePath)

            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val matrix = Matrix()

            when(orientation){
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
            }

            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0,0 , bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()

            return rotatedBitmap
        }
    }
}