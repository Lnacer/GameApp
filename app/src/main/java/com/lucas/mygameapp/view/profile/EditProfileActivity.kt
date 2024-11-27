package com.lucas.mygameapp.view.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.ui.AppBarConfiguration
import com.lucas.mygameapp.Integration.Supabase.UserIntegratiom
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.ActivityEditProfileBinding
import com.lucas.mygameapp.model.User
import com.lucas.mygameapp.utils.BitmapUtil
import com.lucas.mygameapp.utils.DateUtil
import com.lucas.mygameapp.view.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlin.concurrent.thread


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    private var picturePath : String? = null
    private lateinit var loggedUser : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loggedUser = MainActivity.loggedUser

        binding.txtUserName.editableText.append(loggedUser.name)

        if (loggedUser!!.picture != null) {
            val bitmap = BitmapFactory.decodeByteArray(loggedUser!!.picture, 0, loggedUser!!.picture!!.size)

            if (bitmap != null) {
                binding.imgUserPicture.setImageBitmap(bitmap)
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {

                val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}${File.separator}mygameapp"

                val picture = contentResolver.openInputStream(uri)?.readBytes()

                loggedUser.picture = picture

                val f = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    "mygameapp"
                )

                if (!f.exists()) {
                    f.mkdir()
                }

                val pathImage = File(path, "user_pic.jpeg")
                val stream = FileOutputStream(pathImage)
                stream.write(picture).run {
                    stream.flush()
                    stream.close()
                }

                picturePath = pathImage.path

                binding.imgUserPicture.setImageURI(uri)
            }
        }

        binding.imgUserPicture.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.llEditProfileBackButton.setOnClickListener {
            finish()
        }

        binding.btnSaveProfile.setOnClickListener {
            if (isBlank(binding.txtUserName.text.toString())) {
                Toast.makeText(applicationContext, "Please fill in a name", Toast.LENGTH_SHORT).show()
            }
            else {

                loggedUser.name = binding.txtUserName.text.toString().trim()
                loggedUser.picturePath = picturePath

                thread {

                    UserIntegratiom.upsertUser(loggedUser)

                    UserIntegratiom.getUser(loggedUser.email!!)

                    val database = Database.getInstance(applicationContext)
                    database.userDao().updateUser(loggedUser)

                    runOnUiThread {
                        val resultIntent = Intent()
                        resultIntent.putExtra("edited", true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }

    }

    private fun isBlank(text : String?) : Boolean {
        return text == null || text.trim() == ""
    }
}